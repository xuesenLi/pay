package com.lxs.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Mr.Li
 * @date 2019/12/27 - 17:17
 */
@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayAccountConfig {

    private String appId;

    private String privatekey;

    private String publicKey;

    private String notifyUrl;

    private String returnUrl;
}
