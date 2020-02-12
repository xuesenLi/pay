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
     * @param orderId
     * @param amount
     * @param bestPayTypeEnum
     * @return
     */
    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("amount") BigDecimal amount,
                               @RequestParam("payType") BestPayTypeEnum bestPayTypeEnum){
        PayResponse response = payService.create(orderId, amount, bestPayTypeEnum);
        Map<String, String> map = new HashMap();

        //支付方式不同，渲染方式不同
        //微信返回一个二维码地址   WXPAY_NATIVE使用codeUrl
        //支付宝返回一个表单，我们将他渲染到HTML中即可  ALIPAY_PC 使用 body
        if(bestPayTypeEnum == BestPayTypeEnum.WXPAY_NATIVE){
            map.put("codeUrl", response.getCodeUrl());
            map.put("orderId", orderId);
            map.put("returnUrl", wxAccountConfig.getReturnUrl());
            return new ModelAndView("createForWxNative", map);
        }else if(bestPayTypeEnum == BestPayTypeEnum.ALIPAY_PC){
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
