package com.sami9889.aiphonesupport.service;

import com.sami9889.aiphonesupport.dto.ClientRegistrationRequest;
import com.sami9889.aiphonesupport.domain.Client;
import com.sami9889.aiphonesupport.domain.PhoneNumberAssignment;
import com.sami9889.aiphonesupport.repository.ClientRepository;
import com.sami9889.aiphonesupport.repository.PhoneNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class PhoneNumberProvisioningService {

    private final ClientRepository clientRepository;
    private final PhoneNumberRepository phoneNumberRepository;

    @Value("${app.telephony.asterisk.enabled:false}")
    private boolean outboundGatewayEnabled;

    private static final AtomicLong NUMBER_SEQUENCE = new AtomicLong(5000000L);

    public Client registerClient(ClientRegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Client registration request is required.");
        }

        if (clientRepository.findByEmail(request.email().trim()).isPresent()) {
            throw new IllegalArgumentException("A client with this email already exists.");
        }

        String phoneNumber;
        if (StringUtils.hasText(request.phoneNumber())) {
            phoneNumber = normalizeNumber(request.phoneNumber());
            if (phoneNumberRepository.findByNumber(phoneNumber).isPresent()) {
                throw new IllegalArgumentException("This phone number is already assigned.");
            }
        } else if (outboundGatewayEnabled) {
            throw new IllegalArgumentException("A Telstra-provisioned caller ID is required when outbound dialing is enabled.");
        } else {
            phoneNumber = generateNumber(request.countryCode());
        }

        Client client = new Client();
        client.setClientCode(UUID.randomUUID().toString());
        client.setCompanyName(request.companyName());
        client.setContactName(request.contactName());
        client.setEmail(request.email());
        client.setCountryCode(request.countryCode());
        client.setUseCase(request.useCase());
        client.setStatus("ACTIVE");
        client.setCreatedAt(LocalDateTime.now());

        Client savedClient = clientRepository.save(client);

        PhoneNumberAssignment assignment = new PhoneNumberAssignment();
        assignment.setNumber(phoneNumber);
        assignment.setClientId(savedClient.getId());
        assignment.setRegion(request.countryCode());
        assignment.setStatus("ASSIGNED");
        assignment.setAssignedAt(LocalDateTime.now());
        phoneNumberRepository.save(assignment);

        return savedClient;
    }

    public Optional<Client> findClientByPhoneNumber(String calledNumber) {
        if (!StringUtils.hasText(calledNumber)) {
            return Optional.empty();
        }
        return phoneNumberRepository.findByNumberAndStatus(normalizeNumber(calledNumber), "ASSIGNED")
                .flatMap(assignment -> clientRepository.findById(assignment.getClientId()));
    }

    public List<PhoneNumberAssignment> getNumbersForClient(Long clientId) {
        return phoneNumberRepository.findByClientId(clientId);
    }

    public String getPrimaryAssignedNumber(Long clientId) {
        return phoneNumberRepository.findFirstByClientIdAndStatusOrderByAssignedAtAsc(clientId, "ASSIGNED")
                .map(PhoneNumberAssignment::getNumber)
                .orElseThrow(() -> new IllegalArgumentException("Client has no active assigned phone number: " + clientId));
    }

    private String generateNumber(String countryCode) {
        String region = StringUtils.hasText(countryCode) ? countryCode.trim().toUpperCase() : "US";
        long sequence = NUMBER_SEQUENCE.incrementAndGet();

        String areaCode = switch (region) {
            case "CA" -> "416";
            case "GB" -> "20";
            case "AU" -> "283";
            default -> "415";
        };

        return "+1" + areaCode + sequence;
    }

    private String normalizeNumber(String rawNumber) {
        if (!StringUtils.hasText(rawNumber)) {
            return rawNumber;
        }

        String digits = rawNumber.replaceAll("[^0-9+]", "");
        return digits.startsWith("+") ? digits : "+" + digits;
    }
}
