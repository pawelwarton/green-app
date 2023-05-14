package com.example.greenapp.transactions.endec;

import com.example.greenapp.BadRequestException;

public class InvalidTransactionAmount extends BadRequestException {
  private static final String DEFAULT_MESSAGE = "Invalid transaction amount";

  public InvalidTransactionAmount() {
    this(DEFAULT_MESSAGE);
  }

  public InvalidTransactionAmount(String message) {
    this(message, null);
  }

  public InvalidTransactionAmount(Throwable cause) {
    this(DEFAULT_MESSAGE, cause);
  }

  public InvalidTransactionAmount(String message, Throwable cause) {
    super(message == null ? DEFAULT_MESSAGE : message, cause);
  }
}
