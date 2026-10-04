package com.sami9889.aiphonesupport.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(prefix = "app.events.kafka", name = "enabled", havingValue = "true")
public class KafkaTopicConfiguration {

    @Bean
    NewTopic callEventsTopic(
            @Value("${app.events.kafka.topic:call-events}") String topic,
            @Value("${app.events.kafka.partitions:3}") int partitions,
            @Value("${app.events.kafka.replicas:1}") int replicas) {
        return TopicBuilder.name(topic).partitions(partitions).replicas(replicas).build();
    }
}