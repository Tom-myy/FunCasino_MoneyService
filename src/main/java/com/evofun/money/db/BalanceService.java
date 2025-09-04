package com.evofun.money.db;

import com.evofun.money.db.entity.*;
import com.evofun.money.db.repo.GameBalanceTransactionRepo;
import com.evofun.money.db.repo.GeneralBalanceRepo;
import com.evofun.money.db.repo.GeneralBalanceTransactionRepo;
import com.evofun.money.db.repo.GameBalanceRepo;
import com.evofun.money.error.InvalidTransfer;
import com.evofun.money.error.NotEnoughMoneyForTransfer;
import com.evofun.money.error.NotEnoughBalanceException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class BalanceService {
    private final GeneralBalanceRepo generalBalanceRepo;
    private final GameBalanceRepo gameBalanceRepo;
    private final GeneralBalanceTransactionRepo generalBalanceTransactionRepo;
    private final GameBalanceTransactionRepo gameBalanceTransactionRepo;

    public BalanceService(GeneralBalanceRepo generalBalanceRepo, GameBalanceRepo gameBalanceRepo, GeneralBalanceTransactionRepo generalBalanceTransactionRepo, GameBalanceTransactionRepo gameBalanceTransactionRepo) {
        this.generalBalanceRepo = generalBalanceRepo;
        this.gameBalanceRepo = gameBalanceRepo;
        this.generalBalanceTransactionRepo = generalBalanceTransactionRepo;
        this.gameBalanceTransactionRepo = gameBalanceTransactionRepo;
    }

    @Transactional
    public void createUserBalance(UUID userId) {
        GeneralBalance generalBalance = new GeneralBalance(userId);
        GameBalance gameBalance = new GameBalance(userId);

        generalBalanceRepo.save(generalBalance);
        gameBalanceRepo.save(gameBalance);
    }

    @Transactional
    public void transferBetweenBalances(UUID userId, BalanceType from, BalanceType to, BigDecimal amount) {
        //TODO refactor: divide repeatable code to methods
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
            throw new RuntimeException("Unknown error during transfer.");//TODO handle in RestControllerAdvice
        }
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

    @Transactional
    public void changeGameBalance(UUID userId, BigDecimal amount, GameBalanceTransactionType transactionType) {/// refactor
        /// save balance and balance transaction into DB

        GameBalance gameBalance = gameBalanceRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));//TODO change to custom Ex

        if(gameBalance.getGameBalance().compareTo(amount.abs()) >= 0) {
            gameBalance.setGameBalance(gameBalance.getGameBalance().add(amount));
            gameBalanceRepo.save(gameBalance);

            GameBalanceTransaction gameBalanceTransaction = new GameBalanceTransaction (
                    userId,
                    amount,
                    transactionType//todo RFI - type: BET
            );
            gameBalanceTransactionRepo.save(gameBalanceTransaction);
        } else {
            throw new NotEnoughBalanceException("Player doesn't have enough money for his bet. ",
                    "You don't have enough money for this bet.");
        }
    }

    @Transactional
    public void returnBet(UUID userId, BigDecimal amount, GameBalanceTransactionType transactionType) {/// refactor
    /// save balance and balance transaction into DB

        GameBalance gameBalance = gameBalanceRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));//TODO change to custom Ex

        gameBalance.setGameBalance(gameBalance.getGameBalance().add(amount));
        gameBalanceRepo.save(gameBalance);

        GameBalanceTransaction gameBalanceTransaction = new GameBalanceTransaction(
                userId,
                amount,
                transactionType//todo RFI - type: BET
        );
        gameBalanceTransactionRepo.save(gameBalanceTransaction);

    }
}