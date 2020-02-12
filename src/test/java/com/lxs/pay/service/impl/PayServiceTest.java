package com.lxs.pay.service.impl;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lxs.pay.PayApplicationTests;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * @author Mr.Li
 * @date 2019/12/3 - 13:59
 */
public class PayServiceTest extends PayApplicationTests {

    @Autowired
    private PayServiceImpl payService;

    @Autowired
    private AmqpTemplate amqpTemplate;



    @Test
    public void create() {
        //BigDecimal.valueOf(0.01);
        //new BigDecimal("0.01");  千万不要用  new BigDecimal(0.01)会精度丢失。

        //payService.create("1232134214", BigDecimal.valueOf(0.01), BestPayTypeEnum.ALIPAY_PC);

    }

    @Test
    public void sendMessage(){

       //amqpTemplate.convertAndSend("payNotify", "hello answer");

    }

}
