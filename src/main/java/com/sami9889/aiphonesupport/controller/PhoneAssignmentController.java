package com.sami9889.aiphonesupport.controller;

import com.sami9889.aiphonesupport.service.AiPhoneSupportService;
import com.sami9889.aiphonesupport.service.TwilioPhoneNumberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PhoneAssignmentController {

    private final TwilioPhoneNumberService phoneNumberService;

    public PhoneAssignmentController(TwilioPhoneNumberService phoneNumberService) {
        this.phoneNumberService = phoneNumberService;
    }

    @PostMapping(value = "/phone/assign", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> assignSupportNumber() {
        return ResponseEntity.ok(phoneNumberService.assignSupportNumber());
    }
}
