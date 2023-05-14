package com.example.greenapp;

public class BadRequestException extends RuntimeException {

  public BadRequestException(String message) {
    this(message, null);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause, true, Config.WRITABLE_STACK_TRACE);
  }

}
