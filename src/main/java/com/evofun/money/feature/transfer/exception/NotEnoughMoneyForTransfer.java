package com.evofun.money.feature.transfer.exception;

import com.evofun.money.shared.domain.model.enums.BalanceType;
import lombok.Getter;

@Getter
public class NotEnoughMoneyForTransfer extends RuntimeException {
    private final BalanceType balanceType;
    public NotEnoughMoneyForTransfer(BalanceType balanceType) {
        this.balanceType = balanceType;
    }
}
