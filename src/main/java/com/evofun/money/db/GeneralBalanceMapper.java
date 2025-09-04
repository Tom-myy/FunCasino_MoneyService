package com.evofun.money.db;


import com.evofun.money.db.dto.GeneralBalanceDto;
import com.evofun.money.db.entity.GeneralBalance;

public class GeneralBalanceMapper {
    public static GeneralBalanceDto mapToUserBalanceDto (GeneralBalance generalBalance) {
        GeneralBalanceDto generalBalanceDto = new GeneralBalanceDto();
        generalBalanceDto.setUserId(generalBalance.getUserId());
        generalBalanceDto.setBalance(generalBalance.getBalance());
        return generalBalanceDto;
    }
}
