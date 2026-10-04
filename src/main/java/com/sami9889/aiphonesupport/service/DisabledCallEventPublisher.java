package com.sami9889.aiphonesupport.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.events.kafka", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DisabledCallEventPublisher implements CallEventPublisher {

    @Override
    public void publish(CallEvent event) {
    }
}