package com.evofun.money.shared.error;

import com.evofun.money.domain.model.enums.BalanceType;
import lombok.Getter;

@Getter
public class NotEnoughMoneyForTransfer extends RuntimeException {
    private final BalanceType balanceType;
    public NotEnoughMoneyForTransfer(BalanceType balanceType) {
        this.balanceType = balanceType;
    }
}
