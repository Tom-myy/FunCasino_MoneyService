package com.evofun.money.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "general_balance_transactions")
public class GeneralBalanceTransaction {

    @Id
    @Column(name = "transaction_id", nullable = false, updatable = false)
    private UUID transactionId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal balanceDelta;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, updatable = false)
    private GeneralBalanceTransactionType transactionType;

    @Column(name = "context", updatable = false)
    private String context;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (transactionId == null) transactionId = UUID.randomUUID();
    }

    public GeneralBalanceTransaction(UUID userId, BigDecimal balanceDelta, GeneralBalanceTransactionType transactionType, String context) {
        this.userId = userId;
        this.balanceDelta = balanceDelta;
        this.transactionType = transactionType;
        this.context = context;
    }
}