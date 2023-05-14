package com.example.greenapp.endec;

import com.example.greenapp.BadRequestException;

public class InvalidJsonException extends BadRequestException {
  public InvalidJsonException(String message) {
    super(message);
  }
}
