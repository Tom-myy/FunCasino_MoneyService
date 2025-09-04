package com.evofun.money.db.repo;

import com.evofun.money.db.entity.GeneralBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GeneralBalanceRepo extends JpaRepository<GeneralBalance, UUID> {}