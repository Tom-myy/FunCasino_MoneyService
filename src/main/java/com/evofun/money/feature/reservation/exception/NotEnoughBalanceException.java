package com.evofun.money.feature.reservation.exception;

import com.evofun.money.shared.exception.AppException;

public class NotEnoughBalanceException extends AppException {
  public NotEnoughBalanceException(String developerMessage, String userMessage) {
    super(developerMessage, userMessage);
  }
}