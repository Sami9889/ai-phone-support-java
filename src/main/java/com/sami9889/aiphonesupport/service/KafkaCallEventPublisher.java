package com.sami9889.aiphonesupport.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.events.kafka", name = "enabled", havingValue = "true")
public class KafkaCallEventPublisher implements CallEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public KafkaCallEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.events.kafka.topic:call-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    @Override
    public void publish(CallEvent event) {
        try {
            kafkaTemplate.send(topic, event.callId(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException | RuntimeException exception) {
            log.error("Unable to publish call event {} for call {}", event.eventType(), event.callId(), exception);
        }
    }
}