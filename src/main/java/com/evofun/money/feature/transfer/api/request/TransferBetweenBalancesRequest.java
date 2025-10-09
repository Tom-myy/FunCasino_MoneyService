package com.evofun.money.feature.transfer.api.request;

import com.evofun.money.domain.model.enums.BalanceType;
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
public class TransferBetweenBalancesRequest {
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
