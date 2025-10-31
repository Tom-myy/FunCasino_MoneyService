package com.evofun.money.shared.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_game_balances")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameBalance {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "game_balance", nullable = false)
    private BigDecimal gameBalance;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        gameBalance = BigDecimal.valueOf(0);
    }

    public GameBalance(UUID userId) {
        this.userId = userId;
    }
}