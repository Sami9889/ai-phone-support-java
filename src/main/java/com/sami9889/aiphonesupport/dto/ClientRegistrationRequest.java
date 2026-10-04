package com.sami9889.aiphonesupport.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientRegistrationRequest(
        @NotBlank String companyName,
        @NotBlank String contactName,
        @NotBlank @Email String email,
        @NotBlank String countryCode,
        @NotBlank String useCase
) {
}
