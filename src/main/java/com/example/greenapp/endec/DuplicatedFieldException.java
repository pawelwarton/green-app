package com.example.greenapp.endec;

import com.example.greenapp.BadRequestException;

public class DuplicatedFieldException extends BadRequestException {

  public DuplicatedFieldException(String field) {
    super("Duplicated field: " + field);
  }

}
