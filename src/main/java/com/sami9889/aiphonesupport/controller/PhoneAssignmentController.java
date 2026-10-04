package com.sami9889.aiphonesupport.controller;

import com.sami9889.aiphonesupport.dto.ClientRegistrationRequest;
import com.sami9889.aiphonesupport.model.ClientProfile;
import com.sami9889.aiphonesupport.service.TelnyxNumberProvisioningService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PhoneAssignmentController {

    private final TelnyxNumberProvisioningService telnyxNumberProvisioningService;

    public PhoneAssignmentController(TelnyxNumberProvisioningService telnyxNumberProvisioningService) {
        this.telnyxNumberProvisioningService = telnyxNumberProvisioningService;
    }

    @PostMapping(value = "/clients/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> registerClient(@RequestBody ClientRegistrationRequest request) {
        ClientProfile client = telnyxNumberProvisioningService.registerClient(request);

        return ResponseEntity.ok(Map.of(
                "clientCode", client.getClientCode(),
                "companyName", client.getCompanyName(),
                "phoneNumber", client.getPhoneNumber(),
                "status", client.getTelnyxStatus(),
                "provider", "telnyx"
        ));
    }

    @PostMapping(value = "/phone/assign", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> assignSupportNumber(@RequestBody ClientRegistrationRequest request) {
        ClientProfile client = telnyxNumberProvisioningService.registerClient(request);
        return ResponseEntity.ok(Map.of(
                "phoneNumber", client.getPhoneNumber(),
                "status", "assigned",
                "provider", "telnyx",
                "clientCode", client.getClientCode()
        ));
    }
}
