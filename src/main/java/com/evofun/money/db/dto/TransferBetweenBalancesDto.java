package com.evofun.money.db.dto;

import com.evofun.money.db.BalanceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferBetweenBalancesDto {
    @NotNull
    private UUID userId;
    @NotNull
    private BalanceType from;
    @NotNull
    private BalanceType to;
    @NotNull
    @Positive
    private BigDecimal amount;
}
