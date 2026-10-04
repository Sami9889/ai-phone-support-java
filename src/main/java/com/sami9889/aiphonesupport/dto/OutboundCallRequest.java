package com.sami9889.aiphonesupport.dto;

public record OutboundCallRequest(
        Long clientId,
        String toNumber,
        String message
) {
}
