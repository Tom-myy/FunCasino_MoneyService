package com.evofun.money.shared.exception.exceptionHandler;

import com.evofun.money.feature.reservation.exception.NotEnoughBalanceException;
import com.evofun.money.shared.exception.code.ErrorCode;
import com.evofun.money.shared.exception.model.ErrorDto;
import com.evofun.money.shared.exception.code.ErrorPrefix;
import com.evofun.money.shared.exception.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotEnoughBalanceException.class)
    public ResponseEntity<ErrorDto> handleNotEnoughBalanceException(NotEnoughBalanceException ex) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.BUS);

        log.info("⚠️ {} |ErrorId: '{}'|", ex.getDeveloperMessage(), errorId);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorDto(
                        ErrorCode.VALIDATION_ERROR,
                        errorId,
                        ex.getUserMessage(),
                        null)
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnexpectedException(Exception ex) {
        String errorId = ExceptionUtils.generateErrorId(ErrorPrefix.UNKNOWN);

        log.error("❌ Unexpected REST exception (errorId: '{}'), msg: '{}'", errorId, ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(
                        ErrorCode.UNKNOWN_ERROR,
                        errorId,
                        "Internal server exception. Please contact support.",
                        null)
                );
    }
}