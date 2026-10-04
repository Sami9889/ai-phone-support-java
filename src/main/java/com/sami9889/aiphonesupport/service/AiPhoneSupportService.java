package com.sami9889.aiphonesupport.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AiPhoneSupportService {

    public String buildGreeting(String callerNumber) {
        return "Thanks for calling support. My name is Ava. Please tell me how I can help today.";
    }

    public String generateReply(String transcript) {
        if (transcript == null || transcript.isBlank()) {
            return "I did not catch that. Please tell me your issue again.";
        }

        String normalized = transcript.toLowerCase(Locale.ROOT);

        if (normalized.contains("billing") || normalized.contains("invoice") || normalized.contains("charge")) {
            return "I can help with billing, invoices, and payment questions. If you need a human review, say connect me to billing support.";
        }

        if (normalized.contains("cancel") || normalized.contains("subscription") || normalized.contains("membership")) {
            return "I can help with plan changes and subscription questions. I can explain your options or connect you to a specialist.";
        }

        if (normalized.contains("technical") || normalized.contains("error") || normalized.contains("bug") || normalized.contains("login")) {
            return "I can help troubleshoot technical issues. Please describe the problem, the app or device, and any error message.";
        }

        if (normalized.contains("human") || normalized.contains("agent") || normalized.contains("person")) {
            return "I can transfer you to a human specialist. Please hold while I connect your call.";
        }

        return "I understand you are asking for support. I can help with billing, subscriptions, technical issues, or account access. Please tell me a little more about your issue.";
    }

    public boolean requiresHumanEscalation(String transcript) {
        if (transcript == null || transcript.isBlank()) {
            return false;
        }

        String normalized = transcript.toLowerCase(Locale.ROOT);
        return normalized.contains("human")
                || normalized.contains("agent")
                || normalized.contains("person")
                || normalized.contains("representative")
                || normalized.contains("connect me")
                || normalized.contains("speak to someone");
    }
}
