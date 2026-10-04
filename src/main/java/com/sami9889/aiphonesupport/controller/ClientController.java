package com.sami9889.aiphonesupport.controller;

import com.sami9889.aiphonesupport.dto.ClientRegistrationRequest;
import com.sami9889.aiphonesupport.domain.Client;
import com.sami9889.aiphonesupport.service.AuditLogService;
import com.sami9889.aiphonesupport.service.PhoneNumberProvisioningService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ClientController {

    private final PhoneNumberProvisioningService phoneNumberProvisioningService;
    private final AuditLogService auditLogService;

    public ClientController(PhoneNumberProvisioningService phoneNumberProvisioningService,
                          AuditLogService auditLogService) {
        this.phoneNumberProvisioningService = phoneNumberProvisioningService;
        this.auditLogService = auditLogService;
    }

    @PostMapping(value = "/clients/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        Client client = phoneNumberProvisioningService.registerClient(request);
        auditLogService.record("CLIENT_REGISTERED", "Client registered and number assigned", client.getId(), null);

        return ResponseEntity.ok(Map.of(
                "clientCode", client.getClientCode(),
                "companyName", client.getCompanyName(),
                "phoneNumber", phoneNumberProvisioningService.getNumbersForClient(client.getId()).getFirst().getNumber(),
                "status", client.getStatus(),
                "provider", "custom-telephony"
        ));
    }
}
