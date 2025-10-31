package com.evofun.money.feature.wallet.infra.kafka.in;

import com.evofun.events.GameFinishedEvent;
import com.evofun.money.shared.domain.model.GameBalanceTransaction;
import com.evofun.money.shared.domain.model.enums.GameBalanceTransactionType;
import com.evofun.money.feature.wallet.app.WalletUseCase;
import com.evofun.money.infrastructure.kafka.dlq.DlqKafkaProducer;
import com.evofun.money.infrastructure.kafka.dlq.GenericDlqEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class GameFinishedListener {
    private final WalletUseCase walletUseCase;
    private final DlqKafkaProducer dlqProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameFinishedListener(WalletUseCase walletUseCase, DlqKafkaProducer dlqProducer) {
        this.walletUseCase = walletUseCase;
        this.dlqProducer = dlqProducer;
    }

    @KafkaListener(topics = "game-finished", groupId = "money-service")
    public void handle(GameFinishedEvent event) {
        if(!event.gameProfit().equals(BigDecimal.ZERO)) {
            GameBalanceTransaction gameBalanceTransaction = new GameBalanceTransaction(
                    event.userId(),
                    event.gameProfit(),
                    GameBalanceTransactionType.WIN,
                    null
            );

            try {
                walletUseCase.changeGameBalance(gameBalanceTransaction);
            } catch (DataIntegrityViolationException ex) {
                GenericDlqEvent dlq;
                try {
                    dlq = new GenericDlqEvent(
                            "GameFinishedEvent",
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
}