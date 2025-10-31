package com.evofun.money.shared.domain.repo;

import com.evofun.money.shared.domain.model.GameBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GameBalanceRepo extends JpaRepository<GameBalance, UUID> {}