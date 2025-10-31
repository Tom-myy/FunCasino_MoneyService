package com.evofun.money.feature.transfer.api;

import com.evofun.money.feature.transfer.app.TransferUseCase;
import com.evofun.money.security.jwt.JwtUserPrincipal;
import com.evofun.money.feature.transfer.api.request.TransferBetweenBalancesRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/money/transfer")
public class TransferController {
    private final TransferUseCase transferUseCase;

    public TransferController(TransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }

    @PostMapping("/transferBetweenBalances")
    public ResponseEntity<?> transferBetweenBalances(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody TransferBetweenBalancesRequest request) {
        transferUseCase.transferBetweenBalances(
                principal.getUserId(),
                request.getFrom(),
                request.getTo(),
                request.getAmount()
        );

        return ResponseEntity.ok().build();
    }
}
