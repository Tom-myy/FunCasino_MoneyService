package com.evofun.money.error;

public class InvalidTransfer extends RuntimeException {
    public InvalidTransfer(String message) {
        super(message);
    }
}
