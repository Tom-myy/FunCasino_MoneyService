package com.evofun.money.db;

import com.evofun.money.db.dto.GameBalanceDto;
import com.evofun.money.db.entity.GameBalance;

public class GameBalanceMapper {
    public static GameBalanceDto mapToGameBalanceDto (GameBalance gameBalance) {
        GameBalanceDto gameBalanceDto = new GameBalanceDto();
        gameBalanceDto.setUserId(gameBalance.getUserId());
        gameBalanceDto.setBalance(gameBalance.getGameBalance());
        return gameBalanceDto;
    }
}