package com.evofun.money.kafka;

import com.evofun.events.GameFinishedEvent;
import com.evofun.money.db.entity.GameBalanceTransaction;
import com.evofun.money.db.entity.GameBalanceTransactionType;
import com.evofun.money.db.BalanceService;
import com.evofun.money.kafka.dlq.DlqKafkaProducer;
import com.evofun.money.kafka.dlq.GenericDlqEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class GameFinishedListener {

    private final BalanceService balanceService;
    private final DlqKafkaProducer dlqProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameFinishedListener(BalanceService balanceService, DlqKafkaProducer dlqProducer) {
        this.balanceService = balanceService;
        this.dlqProducer = dlqProducer;
    }

    @KafkaListener(topics = "game-finished", groupId = "money-service")
    public void handle(GameFinishedEvent event) {
        System.out.println("Received 'game-finished' event - gonna change balance");

        if(!event.gameProfit().equals(BigDecimal.ZERO)) {
            GameBalanceTransaction gameBalanceTransaction = new GameBalanceTransaction(
                    event.userId(),
                    event.gameProfit(),
                    GameBalanceTransactionType.WIN,
                    null
            );

            try {
                balanceService.changeGameBalance(gameBalanceTransaction);
            } catch (DataIntegrityViolationException ex) {
                GenericDlqEvent dlq;
                try {
                    dlq = new GenericDlqEvent(
                            "GameFinishedEvent",
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
}
