package com.evofun.money.feature.wallet.app.mapper;

import com.evofun.money.feature.wallet.api.response.GameBalanceResponse;
import com.evofun.money.domain.model.GameBalance;

public class GameBalanceMapper {
    public static GameBalanceResponse mapToGameBalanceDto (GameBalance gameBalance) {
        GameBalanceResponse gameBalanceResponse = new GameBalanceResponse();
        gameBalanceResponse.setUserId(gameBalance.getUserId());
        gameBalanceResponse.setBalance(gameBalance.getGameBalance());
        return gameBalanceResponse;
    }
}