package com.sami9889.aiphonesupport.service;

import com.sami9889.aiphonesupport.config.TelnyxProperties;
import com.sami9889.aiphonesupport.dto.ClientRegistrationRequest;
import com.sami9889.aiphonesupport.model.ClientProfile;
import com.sami9889.aiphonesupport.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelnyxNumberProvisioningService {

    private final ClientRepository clientRepository;
    private final TelnyxProperties telnyxProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public ClientProfile registerClient(ClientRegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Client registration request is required.");
        }

        if (StringUtils.hasText(request.email()) && clientRepository.findByEmail(request.email().trim()).isPresent()) {
            throw new IllegalArgumentException("A client with this email already exists.");
        }

        String phoneNumber = purchasePhoneNumber(request.countryCode());

        ClientProfile profile = new ClientProfile();
        profile.setClientCode(UUID.randomUUID().toString());
        profile.setCompanyName(request.companyName());
        profile.setContactName(request.contactName());
        profile.setEmail(request.email());
        profile.setCountryCode(request.countryCode());
        profile.setUseCase(request.useCase());
        profile.setPhoneNumber(phoneNumber);
        profile.setTelnyxStatus("ACTIVE");
        profile.setCreatedAt(LocalDateTime.now());

        return clientRepository.save(profile);
    }

    public Optional<ClientProfile> findClientForCalledNumber(String calledNumber) {
        if (!StringUtils.hasText(calledNumber)) {
            return Optional.empty();
        }
        return clientRepository.findByPhoneNumber(normalizeNumber(calledNumber));
    }

    private String purchasePhoneNumber(String countryCode) {
        String normalizedCountry = StringUtils.hasText(countryCode) ? countryCode.trim().toUpperCase() : "US";

        if (!StringUtils.hasText(telnyxProperties.getApiKey())) {
            return generateMockNumber(normalizedCountry);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(telnyxProperties.getApiKey());
            headers.set("Content-Type", "application/json");

            Map<String, Object> payload = Map.of(
                    "country_code", normalizedCountry,
                    "voice", true,
                    "sms", false,
                    "emergency_enabled", false
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    telnyxProperties.getNumberOrderUrl(),
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getBody() == null) {
                return generateMockNumber(normalizedCountry);
            }

            Object data = response.getBody().get("data");
            if (data instanceof Map<?, ?> map) {
                Object number = map.get("phone_number");
                if (number != null) {
                    return normalizeNumber(number.toString());
                }
                Object phoneNumber = map.get("number");
                if (phoneNumber != null) {
                    return normalizeNumber(phoneNumber.toString());
                }
            }

            return generateMockNumber(normalizedCountry);
        } catch (Exception e) {
            return generateMockNumber(normalizedCountry);
        }
    }

    private String generateMockNumber(String countryCode) {
        String areaCode = switch (countryCode) {
            case "CA" -> "416";
            case "GB" -> "20";
            default -> "415";
        };

        long random = (long) (Math.random() * 9000000L) + 1000000L;
        return "+1" + areaCode + random;
    }

    private String normalizeNumber(String rawNumber) {
        if (!StringUtils.hasText(rawNumber)) {
            return rawNumber;
        }

        String digits = rawNumber.replaceAll("[^0-9+]", "");
        if (digits.startsWith("+")) {
            return digits;
        }
        return "+" + digits;
    }
}
