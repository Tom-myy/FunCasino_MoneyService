package com.evofun.money.feature.transfer.app;

import com.evofun.money.shared.domain.model.enums.BalanceType;
import com.evofun.money.shared.domain.repo.GameBalanceRepo;
import com.evofun.money.shared.domain.repo.GameBalanceTransactionRepo;
import com.evofun.money.shared.domain.repo.GeneralBalanceRepo;
import com.evofun.money.shared.domain.repo.GeneralBalanceTransactionRepo;
import com.evofun.money.shared.domain.model.GameBalance;
import com.evofun.money.shared.domain.model.GeneralBalance;
import com.evofun.money.feature.transfer.exception.InvalidTransfer;
import com.evofun.money.feature.transfer.exception.NotEnoughMoneyForTransfer;
import com.evofun.money.shared.domain.model.GameBalanceTransaction;
import com.evofun.money.shared.domain.model.enums.GameBalanceTransactionType;
import com.evofun.money.shared.domain.model.GeneralBalanceTransaction;
import com.evofun.money.shared.domain.model.enums.GeneralBalanceTransactionType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransferUseCase {
    private final GeneralBalanceRepo generalBalanceRepo;
    private final GameBalanceRepo gameBalanceRepo;
    private final GeneralBalanceTransactionRepo generalBalanceTransactionRepo;
    private final GameBalanceTransactionRepo gameBalanceTransactionRepo;

    public TransferUseCase(GeneralBalanceRepo generalBalanceRepo, GameBalanceRepo gameBalanceRepo, GeneralBalanceTransactionRepo generalBalanceTransactionRepo, GameBalanceTransactionRepo gameBalanceTransactionRepo) {
        this.generalBalanceRepo = generalBalanceRepo;
        this.gameBalanceRepo = gameBalanceRepo;
        this.generalBalanceTransactionRepo = generalBalanceTransactionRepo;
        this.gameBalanceTransactionRepo = gameBalanceTransactionRepo;
    }

    @Transactional
    public void transferBetweenBalances(UUID userId, BalanceType from, BalanceType to, BigDecimal amount) {
        //TODO refactor
        if (from == to) {
            throw new InvalidTransfer("The same wallet type is not allowed for transfer.");//TODO handle in RestControllerAdvice
        }

        if (from == BalanceType.GENERAL_BALANCE && to == BalanceType.GAME_BALANCE) {
            GeneralBalance generalBalance = generalBalanceRepo.findById(userId).orElseThrow();

            if (amount.compareTo(generalBalance.getBalance()) > 0) {
                throw new NotEnoughMoneyForTransfer(from);//TODO handle in RestControllerAdvice
            }

            generalBalance.setBalance(generalBalance.getBalance().subtract(amount));
            generalBalanceRepo.save(generalBalance);

            GeneralBalanceTransaction generalBalanceTransaction = new GeneralBalanceTransaction(
                    userId,
                    amount,
                    GeneralBalanceTransactionType.TRANSFER,
                    null);
            generalBalanceTransactionRepo.save(generalBalanceTransaction);

            GameBalance gameBalance = gameBalanceRepo.findById(userId).orElseThrow();
            gameBalance.setGameBalance(gameBalance.getGameBalance().add(amount));
            gameBalanceRepo.save(gameBalance);

            GameBalanceTransaction gameBalanceTransaction = new GameBalanceTransaction(
                    userId,
                    amount,
                    GameBalanceTransactionType.TRANSFER,
                    null);
            gameBalanceTransactionRepo.save(gameBalanceTransaction);

        } else if (from == BalanceType.GAME_BALANCE && to == BalanceType.GENERAL_BALANCE) {
            GameBalance gameBalance = gameBalanceRepo.findById(userId).orElseThrow();

            if (amount.compareTo(gameBalance.getGameBalance()) > 0) {
                throw new NotEnoughMoneyForTransfer(from);//TODO handle in RestControllerAdvice
            }

            gameBalance.setGameBalance(gameBalance.getGameBalance().subtract(amount));
            gameBalanceRepo.save(gameBalance);

            GameBalanceTransaction gameBalanceTransaction = new GameBalanceTransaction(
                    userId,
                    amount,
                    GameBalanceTransactionType.TRANSFER,
                    null);
            gameBalanceTransactionRepo.save(gameBalanceTransaction);

            GeneralBalance generalBalance = generalBalanceRepo.findById(userId).orElseThrow();
            generalBalance.setBalance(generalBalance.getBalance().add(amount));
            generalBalanceRepo.save(generalBalance);

            GeneralBalanceTransaction generalBalanceTransaction = new GeneralBalanceTransaction(
                    userId,
                    amount,
                    GeneralBalanceTransactionType.TRANSFER,
                    null);
            generalBalanceTransactionRepo.save(generalBalanceTransaction);
        } else {
            throw new RuntimeException("Unknown exception during transfer.");//TODO handle in RestControllerAdvice
        }
    }
}