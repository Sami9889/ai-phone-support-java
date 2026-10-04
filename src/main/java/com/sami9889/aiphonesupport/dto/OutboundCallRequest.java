package com.sami9889.aiphonesupport.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OutboundCallRequest(
        @NotNull Long clientId,
        @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{7,14}$") String toNumber,
        String message
) {
}
