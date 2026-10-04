package com.sami9889.aiphonesupport.model;

import java.time.Instant;

public record SupportCallSession(
        String callSid,
        String from,
        String to,
        Instant startedAt,
        String status,
        String lastTranscript
) {
}
