package com.evofun.money.feature.wallet.api;

import com.evofun.money.feature.wallet.app.WalletUseCase;
import com.evofun.money.feature.wallet.app.mapper.GameBalanceMapper;
import com.evofun.money.shared.domain.model.GameBalance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/money/wallet")
public class WalletController {
    private final WalletUseCase walletUseCase;

    public WalletController(WalletUseCase walletUseCase) {
        this.walletUseCase = walletUseCase;
    }

    @GetMapping("/gameBalanceById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        Optional<GameBalance> optionalGameBalance = walletUseCase.getGameBalanceById(id);

        if (optionalGameBalance.isPresent()) {
            return ResponseEntity.ok(GameBalanceMapper.mapToGameBalanceDto(optionalGameBalance.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}