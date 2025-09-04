package com.evofun.money.kafka;

import com.evofun.events.UserRegisteredEvent;
import com.evofun.money.db.BalanceService;
import com.evofun.money.kafka.dlq.DlqKafkaProducer;
import com.evofun.money.kafka.dlq.GenericDlqEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
public class UserRegisteredListener {

    private final BalanceService balanceService;
    private final DlqKafkaProducer dlqProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserRegisteredListener(BalanceService balanceService, DlqKafkaProducer dlqProducer) {
        this.balanceService = balanceService;
        this.dlqProducer = dlqProducer;
    }

    @KafkaListener(topics = "user-registered", groupId = "money-service")
    public void handle(UserRegisteredEvent event) {
        System.out.println("Received 'user-registered' event - gonna create user balance");

        try {
            balanceService.createUserBalance(event.userId());
        } catch (DataIntegrityViolationException ex) {
            GenericDlqEvent dlq;
            try {
                dlq = new GenericDlqEvent(
                        "UserRegisteredEvent",
                        event.userId(),
                        objectMapper.writeValueAsString(event),
                        ex.getClass().getSimpleName(),
                        ex.getMessage(),
//                        Instant.now()
                        OffsetDateTime.now()
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            dlqProducer.sendDlq(dlq);
        }
    }
}
