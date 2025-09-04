package com.evofun.money.controller;

import com.evofun.money.error.*;
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

        log.error("❌ Unexpected REST error (errorId: '{}'), msg: '{}'", errorId, ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(
                        ErrorCode.UNKNOWN_ERROR,
                        errorId,
                        "Internal server error. Please contact support.",
                        null)
                );
    }
}