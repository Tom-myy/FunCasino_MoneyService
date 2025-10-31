package com.evofun.money.shared.domain.repo;

import com.evofun.money.shared.domain.model.GeneralBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GeneralBalanceRepo extends JpaRepository<GeneralBalance, UUID> {}