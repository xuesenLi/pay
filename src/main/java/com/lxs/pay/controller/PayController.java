package com.lxs.pay.controller;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.lxs.pay.config.WxAccountConfig;
import com.lxs.pay.form.PayForm;
import com.lxs.pay.pojo.PayInfo;
import com.lxs.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Li
 * @date 2019/12/3 - 14:42
 */
@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    private PayService payService;

    /**
     * 在BestPayconfig中声明的bean  未来拿到 wxPayConfig.ReturnUrl
     */
    @Autowired
    private WxAccountConfig wxAccountConfig;

    /**
     * 支付请求  ：  跳转
     * @param payForm
     * @return
     */
    @PostMapping("/create")
    public ModelAndView create(@RequestBody PayForm payForm){
        log.info("payForm = {},  orderId = {}", payForm, payForm.getOrderId());
        PayResponse response = payService.create(payForm.getOrderId(), payForm.getAmount(), payForm.getPayType());
        Map<String, String> map = new HashMap();

        //支付方式不同，渲染方式不同
        //微信返回一个二维码地址   WXPAY_NATIVE使用codeUrl
        //支付宝返回一个表单，我们将他渲染到HTML中即可  ALIPAY_PC 使用 body
        if(payForm.getPayType() == BestPayTypeEnum.WXPAY_NATIVE){
            map.put("codeUrl", response.getCodeUrl());
            map.put("orderId", payForm.getOrderId());
            map.put("returnUrl", wxAccountConfig.getReturnUrl());
            return new ModelAndView("createForWxNative", map);
        }else if(payForm.getPayType() == BestPayTypeEnum.ALIPAY_PC){
            map.put("body", response.getBody());
            return new ModelAndView("createForAlipayPC", map);
        }
        throw new RuntimeException("暂不支持的支付类型");
    }


    /**
     * 异步通知处理
     * @param notifyData
     */
    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData){
        return payService.asyncNotify(notifyData);
    }

    @GetMapping("queryByOrderId")
    @ResponseBody
    public PayInfo queryByOrderId(@RequestParam String orderId){
        return payService.queryByOrderId(orderId);
    }


}
