package com.sami9889.aiphonesupport.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AiPhoneSupportService {

    public String buildInitialGreeting(String callerNumber) {
        return "Thanks for calling AI support. " +
                "My name is Ava. Please tell me how I can help you today. " +
                "You can also press 0 to speak with a human specialist.";
    }

    public String generateReply(String transcript) {
        if (transcript == null || transcript.isBlank()) {
            return "I did not catch that. Please tell me your issue again.";
        }

        String normalized = transcript.toLowerCase(Locale.ROOT);

        if (normalized.contains("billing") || normalized.contains("invoice") || normalized.contains("charge")) {
            return "I can help with billing. I can explain your invoice, recent charges, or payment status. " +
                    "If you need a human review, say connect me to a billing specialist.";
        }

        if (normalized.contains("cancel") || normalized.contains("subscription")) {
            return "I can assist with account changes and subscriptions. " +
                    "I can help you review your plan, discuss cancellation options, or connect you to a specialist.";
        }

        if (normalized.contains("technical") || normalized.contains("error") || normalized.contains("bug") || normalized.contains("login")) {
            return "I can help troubleshoot technical issues. Describe the problem, the app or device, and the error message.";
        }

        if (normalized.contains("human") || normalized.contains("agent") || normalized.contains("person")) {
            return "I can transfer you to a human specialist. Please hold while I connect your call.";
        }

        return "I understand this is a support request. I can help with billing, subscriptions, technical issues, or account access. " +
                "Please tell me a little more about your issue so I can guide you.";
    }
}
