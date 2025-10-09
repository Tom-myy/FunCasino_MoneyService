package com.evofun.money.feature.reservation.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoneyReservationRequest {
    UUID userId;
    BigDecimal amount;
}
