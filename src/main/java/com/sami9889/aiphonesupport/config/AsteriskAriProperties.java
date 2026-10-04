package com.sami9889.aiphonesupport.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.telephony.asterisk")
public class AsteriskAriProperties {
    private String baseUrl = "http://asterisk:8088/ari/";
    private String username;
    private String password;
    private String endpointPrefix = "PJSIP/";
    private String endpointSuffix = "@telstra";
    private String context = "ai-outbound";
    private String extension = "s";
    private int priority = 1;
    private int timeoutSeconds = 45;
}