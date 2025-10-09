package com.evofun.money.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_general_balances")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GeneralBalance {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "created_at ", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at ", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        balance = BigDecimal.valueOf(1000);
    }

    public GeneralBalance(UUID userId) {
        this.userId = userId;
    }
}