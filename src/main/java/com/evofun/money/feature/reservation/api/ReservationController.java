package com.evofun.money.feature.reservation.api;

import com.evofun.money.feature.reservation.app.ReservationUseCase;
import com.evofun.money.feature.transfer.api.request.BetCancelRequest;
import com.evofun.money.feature.reservation.api.request.MoneyReservationRequest;
import com.evofun.money.shared.domain.model.enums.GameBalanceTransactionType;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/money/reservation")
public class ReservationController {
    private final ReservationUseCase reservationUseCase;

    public ReservationController(ReservationUseCase reservationUseCase) {
        this.reservationUseCase = reservationUseCase;
    }

    @PostMapping("/reserveMoneyForBet")
    public ResponseEntity<?> reserveMoneyForBet(@Valid @RequestBody MoneyReservationRequest request) {//TODO @Valid
        BigDecimal negativeAmount = BigDecimal.ZERO.subtract(request.getAmount());
        reservationUseCase.reserveMoneyForBet(request.getUserId(), negativeAmount, GameBalanceTransactionType.BET);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancelBet")
    public ResponseEntity<?> cancelBet(@Valid @RequestBody BetCancelRequest request) {//TODO @Valid
        reservationUseCase.returnBet(request.getUserId(), request.getAmount(), GameBalanceTransactionType.BET_CANCEL);

        return ResponseEntity.ok().build();
    }
}
