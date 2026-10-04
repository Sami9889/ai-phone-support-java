package com.sami9889.aiphonesupport.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.telephony.asterisk", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DisabledOutboundCallGateway implements OutboundCallGateway {

    @Override
    public String originate(String callId, String fromNumber, String toNumber) {
        throw new OutboundTelephonyUnavailableException("Outbound calling is not configured.");
    }
}