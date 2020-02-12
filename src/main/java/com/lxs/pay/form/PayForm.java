package com.lxs.pay.form;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Mr.Li
 * @date 2020/2/12 - 11:49
 */
@Data
public class PayForm {
    private String orderId;

    private String orderName;

    private BigDecimal amount;

    private BestPayTypeEnum payType;
}
