package com.lxs.pay.enums;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import lombok.Data;
import lombok.Getter;

/**
 * @author Mr.Li
 * @date 2019/12/27 - 11:18
 *
 *  //支付平台  1 支付宝   2-微信
 */
@Getter
public enum  PayPlatformEnum {
    ALIPAY(1),
    WX(2),
    ;

    Integer code;
    PayPlatformEnum(Integer code){
        this.code = code;
    }

    /**
     * 通过 BestPayTypeEnum 枚举 返回我们需要的 PayPlatformEnum 枚举
     * @param bestPayTypeEnum
     * @return
     */
    public static PayPlatformEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum){
       /*if( bestPayTypeEnum.getPlatform().name().equals(PayPlatformEnum.ALIPAY.name()) ) {
           return PayPlatformEnum.ALIPAY;
       }else if(bestPayTypeEnum.getPlatform().name().equals(PayPlatformEnum.WX.name())){
           return PayPlatformEnum.WX;
       }*/

       //优化
        for(PayPlatformEnum payPlatformEnum : PayPlatformEnum.values()){
            if(bestPayTypeEnum.getPlatform().name().equals(payPlatformEnum.name())){
                return payPlatformEnum;
            }
        }
        throw new RuntimeException("错误的支付平台: " + bestPayTypeEnum.name());
    }

}
