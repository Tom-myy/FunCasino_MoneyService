package com.evofun.money.feature.wallet.app;

import com.evofun.money.shared.domain.model.GameBalance;
import com.evofun.money.shared.domain.model.GameBalanceTransaction;
import com.evofun.money.shared.domain.model.GeneralBalance;
import com.evofun.money.shared.domain.repo.GameBalanceRepo;
import com.evofun.money.shared.domain.repo.GameBalanceTransactionRepo;
import com.evofun.money.shared.domain.repo.GeneralBalanceRepo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Component
public class WalletUseCase {
    private final GeneralBalanceRepo generalBalanceRepo;
    private final GameBalanceRepo gameBalanceRepo;
    private final GameBalanceTransactionRepo gameBalanceTransactionRepo;

    public WalletUseCase(GeneralBalanceRepo generalBalanceRepo, GameBalanceRepo gameBalanceRepo, GameBalanceTransactionRepo gameBalanceTransactionRepo) {
        this.generalBalanceRepo = generalBalanceRepo;
        this.gameBalanceRepo = gameBalanceRepo;
        this.gameBalanceTransactionRepo = gameBalanceTransactionRepo;
    }

    @Transactional
    public void createUserBalance(UUID userId) {
        GeneralBalance generalBalance = new GeneralBalance(userId);
        GameBalance gameBalance = new GameBalance(userId);

        generalBalanceRepo.save(generalBalance);
        gameBalanceRepo.save(gameBalance);
    }

    public Optional<GameBalance> getGameBalanceById(UUID id) {
        return gameBalanceRepo.findById(id);
    }

    @Transactional
    public void changeGameBalance(GameBalanceTransaction transaction) {//TODO use method below instead
        /// save game balance transaction into DB and also save balance

        gameBalanceTransactionRepo.save(transaction);

        GameBalance gameBalance = gameBalanceRepo.findById(transaction.getUserId())
                .orElseThrow(() -> new RuntimeException("Balance not found"));

        gameBalance.setGameBalance(gameBalance.getGameBalance().add(transaction.getBalanceDelta()));

        gameBalanceRepo.save(gameBalance);
    }
}