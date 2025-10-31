package com.evofun.money.feature.wallet.app.mapper;


import com.evofun.money.feature.wallet.api.response.GeneralBalanceResponse;
import com.evofun.money.shared.domain.model.GeneralBalance;

public class GeneralBalanceMapper {
    public static GeneralBalanceResponse mapToUserBalanceDto (GeneralBalance generalBalance) {
        GeneralBalanceResponse generalBalanceResponse = new GeneralBalanceResponse();
        generalBalanceResponse.setUserId(generalBalance.getUserId());
        generalBalanceResponse.setBalance(generalBalance.getBalance());
        return generalBalanceResponse;
    }
}
