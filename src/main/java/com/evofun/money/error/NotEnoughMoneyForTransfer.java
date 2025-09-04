package com.evofun.money.error;

import com.evofun.money.db.BalanceType;
import lombok.Getter;

@Getter
public class NotEnoughMoneyForTransfer extends RuntimeException {
    private final BalanceType balanceType;
    public NotEnoughMoneyForTransfer(BalanceType balanceType) {
        this.balanceType = balanceType;
    }
}
