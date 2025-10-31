package com.evofun.money.feature.reservation.app;

import com.evofun.money.shared.domain.model.GameBalance;
import com.evofun.money.shared.domain.model.GameBalanceTransaction;
import com.evofun.money.shared.domain.model.enums.GameBalanceTransactionType;
import com.evofun.money.shared.domain.repo.GameBalanceRepo;
import com.evofun.money.shared.domain.repo.GameBalanceTransactionRepo;
import com.evofun.money.feature.reservation.exception.NotEnoughBalanceException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ReservationUseCase {
    private final GameBalanceRepo gameBalanceRepo;
    private final GameBalanceTransactionRepo gameBalanceTransactionRepo;

    public ReservationUseCase(GameBalanceRepo gameBalanceRepo, GameBalanceTransactionRepo gameBalanceTransactionRepo) {
        this.gameBalanceRepo = gameBalanceRepo;
        this.gameBalanceTransactionRepo = gameBalanceTransactionRepo;
    }

    @Transactional
    public void returnBet(UUID userId, BigDecimal amount, GameBalanceTransactionType transactionType) {
    //TODO save balance and balance transaction into DB

        GameBalance gameBalance = gameBalanceRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));//TODO change to custom Ex

        gameBalance.setGameBalance(gameBalance.getGameBalance().add(amount));
        gameBalanceRepo.save(gameBalance);

        GameBalanceTransaction gameBalanceTransaction = new GameBalanceTransaction(
                userId,
                amount,
                transactionType
        );
        gameBalanceTransactionRepo.save(gameBalanceTransaction);

    }

    @Transactional
    public void reserveMoneyForBet(UUID userId, BigDecimal amount, GameBalanceTransactionType transactionType) {/// refactor
    //TODO save balance and balance transaction into DB

        GameBalance gameBalance = gameBalanceRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));//TODO change to custom Ex

        if(gameBalance.getGameBalance().compareTo(amount.abs()) >= 0) {
            gameBalance.setGameBalance(gameBalance.getGameBalance().add(amount));
            gameBalanceRepo.save(gameBalance);

            GameBalanceTransaction gameBalanceTransaction = new GameBalanceTransaction (
                    userId,
                    amount,
                    transactionType
            );
            gameBalanceTransactionRepo.save(gameBalanceTransaction);
        } else {
            throw new NotEnoughBalanceException("Player doesn't have enough money for his bet. ",
                    "You don't have enough money for this bet.");
        }
    }
}