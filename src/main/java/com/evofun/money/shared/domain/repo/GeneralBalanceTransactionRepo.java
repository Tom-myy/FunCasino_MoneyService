package com.evofun.money.shared.domain.repo;

import com.evofun.money.shared.domain.model.GeneralBalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GeneralBalanceTransactionRepo extends JpaRepository<GeneralBalanceTransaction, UUID> {}