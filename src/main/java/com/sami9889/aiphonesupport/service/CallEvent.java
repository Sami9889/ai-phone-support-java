package com.sami9889.aiphonesupport.service;

import java.time.LocalDateTime;

public record CallEvent(
        String eventType,
        String callId,
        Long clientId,
        String status,
        LocalDateTime occurredAt
) {
}