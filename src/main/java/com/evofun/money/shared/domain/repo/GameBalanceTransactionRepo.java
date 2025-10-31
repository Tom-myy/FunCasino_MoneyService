package com.evofun.money.shared.domain.repo;

import com.evofun.money.shared.domain.model.GameBalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GameBalanceTransactionRepo extends JpaRepository<GameBalanceTransaction, UUID> {}