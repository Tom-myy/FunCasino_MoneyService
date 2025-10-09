package com.evofun.money.shared.kafka.dlq;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GenericDlqEvent(
        String eventType,
        UUID userId,
        String originalPayload,
        String exceptionType,
        String errorMessage,
        OffsetDateTime timestamp
) {}
