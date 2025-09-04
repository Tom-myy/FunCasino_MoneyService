package com.evofun.money.db.repo;

import com.evofun.money.db.entity.GameBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GameBalanceRepo extends JpaRepository<GameBalance, UUID> {}