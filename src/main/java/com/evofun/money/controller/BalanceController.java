package com.evofun.money.controller;

import com.evofun.money.BetCancelRequest;
import com.evofun.money.MoneyReservationRequest;
import com.evofun.money.db.GameBalanceMapper;
import com.evofun.money.db.BalanceService;
import com.evofun.money.db.dto.TransferBetweenBalancesDto;
import com.evofun.money.db.entity.GameBalance;
import com.evofun.money.db.entity.GameBalanceTransactionType;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal")
public class BalanceController {
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/userBalanceById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        Optional<GameBalance> optionalUser = balanceService.getGameBalanceById(id);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(GameBalanceMapper.mapToGameBalanceDto(optionalUser.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reserveMoneyForBet")
    public ResponseEntity<?> reserveMoneyForBet(@Valid @RequestBody MoneyReservationRequest request) {//TODO @Valid
        BigDecimal negativeAmount = BigDecimal.ZERO.subtract(request.getAmount());
        balanceService.changeGameBalance(request.getUserId(), negativeAmount, GameBalanceTransactionType.BET);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancelBet")
    public ResponseEntity<?> cancelBet(@Valid @RequestBody BetCancelRequest request) {//TODO @Valid
        balanceService.returnBet(request.getUserId(), request.getAmount(), GameBalanceTransactionType.BET_CANCEL);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/transferBetweenBalances")
    public ResponseEntity<?> transferBetweenBalances(@Valid @RequestBody TransferBetweenBalancesDto request) {
        balanceService.transferBetweenBalances(
                request.getUserId(),
                request.getFrom(),
                request.getTo(),
                request.getAmount()
        );

        return ResponseEntity.ok().build();
    }
}
