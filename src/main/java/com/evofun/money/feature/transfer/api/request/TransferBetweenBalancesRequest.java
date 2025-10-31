package com.evofun.money.feature.transfer.api.request;

import com.evofun.money.shared.domain.model.enums.BalanceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferBetweenBalancesRequest {
    @NotNull
    private BalanceType from;
    @NotNull
    private BalanceType to;
    @NotNull
    @Positive
    private BigDecimal amount;
}
