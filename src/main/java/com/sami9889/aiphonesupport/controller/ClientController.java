package com.sami9889.aiphonesupport.controller;

import com.sami9889.aiphonesupport.dto.ClientRegistrationRequest;
import com.sami9889.aiphonesupport.model.ClientProfile;
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

    public ClientController(PhoneNumberProvisioningService phoneNumberProvisioningService) {
        this.phoneNumberProvisioningService = phoneNumberProvisioningService;
    }

    @PostMapping(value = "/clients/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        ClientProfile client = phoneNumberProvisioningService.registerClient(request);

        return ResponseEntity.ok(Map.of(
                "clientCode", client.getClientCode(),
                "companyName", client.getCompanyName(),
                "phoneNumber", client.getPhoneNumber(),
                "status", client.getStatus(),
                "provider", "custom-telephony"
        ));
    }
}
