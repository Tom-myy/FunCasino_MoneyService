package com.evofun.money.feature.wallet.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GeneralBalanceResponse {
    private UUID userId;
    private BigDecimal balance;
}
