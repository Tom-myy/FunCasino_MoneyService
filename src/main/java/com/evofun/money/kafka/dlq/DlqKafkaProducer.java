package com.evofun.money.kafka.dlq;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DlqKafkaProducer {
    private final KafkaTemplate<String, GenericDlqEvent> kafkaTemplate;

    public DlqKafkaProducer(KafkaTemplate<String, GenericDlqEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDlq(GenericDlqEvent event) {
        try {
            kafkaTemplate.send("money-service-dlq", event.userId().toString(), event)
                    .get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send dlq kafka event", e);
        }
    }
}