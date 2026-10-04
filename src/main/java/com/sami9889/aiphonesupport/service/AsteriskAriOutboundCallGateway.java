package com.sami9889.aiphonesupport.service;

import com.sami9889.aiphonesupport.config.AsteriskAriProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.telephony.asterisk", name = "enabled", havingValue = "true")
public class AsteriskAriOutboundCallGateway implements OutboundCallGateway {

    private static final Pattern E164_NUMBER = Pattern.compile("^\\+[1-9]\\d{7,14}$");

    private final RestClient.Builder restClientBuilder;
    private final AsteriskAriProperties properties;

    @Override
    public String originate(String callId, String fromNumber, String toNumber) {
        validateConfiguration();
        validateNumber(fromNumber, "Caller ID");
        validateNumber(toNumber, "Destination");

        String endpoint = properties.getEndpointPrefix() + toNumber + properties.getEndpointSuffix();
        try {
            AriChannel channel = restClientBuilder.clone()
                    .baseUrl(properties.getBaseUrl())
                    .build()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path("channels")
                            .queryParam("endpoint", endpoint)
                            .queryParam("extension", properties.getExtension())
                            .queryParam("context", properties.getContext())
                            .queryParam("priority", properties.getPriority())
                            .queryParam("callerId", fromNumber)
                            .queryParam("timeout", properties.getTimeoutSeconds())
                            .queryParam("variables[APP_CALL_ID]", callId)
                            .build())
                    .headers(headers -> headers.setBasicAuth(properties.getUsername(), properties.getPassword()))
                    .retrieve()
                    .body(AriChannel.class);

            if (channel == null || !StringUtils.hasText(channel.id())) {
                throw new OutboundTelephonyUnavailableException("Asterisk did not return an outbound channel ID.");
            }
            return channel.id();
        } catch (RestClientException exception) {
            throw new OutboundTelephonyUnavailableException("Telstra SIP trunk call origination failed.", exception);
        }
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(properties.getBaseUrl())
                || !StringUtils.hasText(properties.getUsername())
                || !StringUtils.hasText(properties.getPassword())
                || !StringUtils.hasText(properties.getEndpointPrefix())
                || !StringUtils.hasText(properties.getEndpointSuffix())
                || !StringUtils.hasText(properties.getContext())
                || !StringUtils.hasText(properties.getExtension())) {
            throw new OutboundTelephonyUnavailableException("Asterisk ARI and Telstra trunk settings are incomplete.");
        }
    }

    private void validateNumber(String number, String label) {
        if (!StringUtils.hasText(number) || !E164_NUMBER.matcher(number).matches()) {
            throw new IllegalArgumentException(label + " must be in E.164 format.");
        }
    }

    private record AriChannel(String id) {
    }
}