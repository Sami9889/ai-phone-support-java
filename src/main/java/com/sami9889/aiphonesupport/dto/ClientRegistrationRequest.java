package com.sami9889.aiphonesupport.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClientRegistrationRequest(
        @NotBlank String companyName,
        @NotBlank String contactName,
        @NotBlank @Email String email,
        @NotBlank String countryCode,
        @NotBlank String useCase,
        @Pattern(regexp = "^$|^\\+[1-9]\\d{7,14}$") String phoneNumber
) {
}
