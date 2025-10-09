package com.evofun.money.feature.wallet.infra.kafka.in;

import com.evofun.events.UserRegisteredEvent;
import com.evofun.money.feature.wallet.app.WalletUseCase;
import com.evofun.money.shared.kafka.dlq.DlqKafkaProducer;
import com.evofun.money.shared.kafka.dlq.GenericDlqEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
public class UserRegisteredListener {
    private final WalletUseCase walletUseCase;
    private final DlqKafkaProducer dlqProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserRegisteredListener(WalletUseCase walletUseCase, DlqKafkaProducer dlqProducer) {
        this.walletUseCase = walletUseCase;
        this.dlqProducer = dlqProducer;
    }

    @KafkaListener(topics = "user-registered", groupId = "money-service")
    public void handle(UserRegisteredEvent event) {
        try {
            walletUseCase.createUserBalance(event.userId());
        } catch (DataIntegrityViolationException ex) {
            GenericDlqEvent dlq;
            try {
                dlq = new GenericDlqEvent(
                        "UserRegisteredEvent",
                        event.userId(),
                        objectMapper.writeValueAsString(event),
                        ex.getClass().getSimpleName(),
                        ex.getMessage(),
                        OffsetDateTime.now()
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            dlqProducer.sendDlq(dlq);
        }
    }
}
