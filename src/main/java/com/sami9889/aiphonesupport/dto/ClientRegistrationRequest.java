package com.sami9889.aiphonesupport.dto;

public record ClientRegistrationRequest(
        String companyName,
        String contactName,
        String email,
        String countryCode,
        String useCase
) {
}
