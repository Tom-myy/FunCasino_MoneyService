package com.evofun.money.db.repo;

import com.evofun.money.db.entity.GameBalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GameBalanceTransactionRepo extends JpaRepository<GameBalanceTransaction, UUID> {}