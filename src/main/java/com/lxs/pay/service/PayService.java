package com.lxs.pay.service;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.lxs.pay.pojo.PayInfo;

import java.math.BigDecimal;

/**
 * @author Mr.Li
 * @date 2019/12/2 - 22:57
 */
public interface PayService {

    /**
     * 创建/发起支付
     * @param orderId
     * @param amount
     */
    PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

    /**
     * 异步通知处理
     * @param notifyData
     */
    String asyncNotify(String notifyData);

    PayInfo queryByOrderId(String orderId);
}
