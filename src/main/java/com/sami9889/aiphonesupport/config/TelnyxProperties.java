package com.sami9889.aiphonesupport.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telnyx")
@Getter
@Setter
public class TelnyxProperties {
    private String apiKey;
    private String publicKey;
    private String phoneNumber;
    private String appUrl;
    private String numberOrderUrl;
}
