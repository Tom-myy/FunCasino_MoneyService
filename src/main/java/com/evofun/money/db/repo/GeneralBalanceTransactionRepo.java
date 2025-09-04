package com.evofun.money.db.repo;

import com.evofun.money.db.entity.GeneralBalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GeneralBalanceTransactionRepo extends JpaRepository<GeneralBalanceTransaction, UUID> {}