package com.lxs.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lxs.pay.dao.PayInfoMapper;
import com.lxs.pay.enums.PayPlatformEnum;
import com.lxs.pay.pojo.PayInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author Mr.Li
 * @date 2019/12/2 - 22:59
 */
@Slf4j
@Service
public class PayServiceImpl implements com.lxs.pay.service.PayService {

    //RabbitMQ  queue
    private final static String QUEUE_PAY_NOTIFY = "payNotify";

    @Autowired
    private BestPayService bestPayService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;


    /**
     * 创建发起支付
     * @param orderId
     * @param amount
     * @return
     */
    @Override
    public PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {


        //判断支付方式 目前支持两种
        if(bestPayTypeEnum != BestPayTypeEnum.WXPAY_NATIVE
        && bestPayTypeEnum != BestPayTypeEnum.ALIPAY_PC){
            throw new RuntimeException("暂不支持的支付类型");
        }

        //写入数据库  OrderStatus 默认未支付
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount);
        payInfoMapper.insertSelective(payInfo);


        PayRequest request = new PayRequest();
        request.setOrderName("8034816-最好的支付sdk");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
        request.setPayTypeEnum(bestPayTypeEnum);

        PayResponse response = bestPayService.pay(request);

        log.info("发起支付的 response = {} ", response);
        return response;
    }


    /**
     * 异步通知处理
     * @param notifyData
     */
    @Override
    public String asyncNotify(String notifyData) {
        //1.签名校验

        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 Response = {}", payResponse);

        //2.金额校验（从数据库里面查询订单， 与异步通知的金额比较）
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        //比较严重（正常情况下不会发生）  发出告警 钉钉或则短信。
        if(payInfo == null){
            throw new RuntimeException("通过OrderNo查询到的结果为null");
        }

        //如果订单支付状态不是“已支付"
        if(!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())){
            //Double类型比较大小，精度不好比较
            if(payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0){
                //告警
                throw new RuntimeException("异步通知中的金额和数据库中的不一致 orderNo = " + payResponse.getOrderId());
            }
            //3. 修改订单支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            //由支付平台产生的交易流水号。
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            payInfo.setUpdateTime(null); //修改时间 交由Mysql 去设置了
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }

        //TODO 由pay 发送MQ消息   mall  接受MQ消息
        //将payInfo 对象传递过去 建议使用json 方便在管控台查看
        amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY, JSON.toJSONString(payInfo));



        if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX){
            //4.告诉微信不要再通知了
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }else if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY){
            //告诉支付宝不要通知了
            return "success";
        }

        throw new RuntimeException("异步通知中错误的支付平台");

    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(orderId));

        return payInfo;
    }


}
