package com.evofun.money.error;

public class NotEnoughBalanceException extends AppException {
  public NotEnoughBalanceException(String developerMessage, String userMessage) {
    super(developerMessage, userMessage);
  }
}