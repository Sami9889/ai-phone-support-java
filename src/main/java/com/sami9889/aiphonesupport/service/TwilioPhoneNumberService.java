package com.sami9889.aiphonesupport.service;

import com.sami9889.aiphonesupport.config.TwilioProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class TwilioPhoneNumberService {

    private final TwilioProperties twilioProperties;

    public TwilioPhoneNumberService(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    public Map<String, String> assignSupportNumber() {
        String phoneNumber = twilioProperties.getPhoneNumber();

        if (!StringUtils.hasText(phoneNumber)) {
            throw new IllegalStateException("TWILIO_PHONE_NUMBER is not configured.");
        }

        return Map.of(
                "phoneNumber", phoneNumber,
                "status", "assigned",
                "provider", "twilio"
        );
    }
}
