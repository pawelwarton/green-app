package com.example.greenapp.atmservice.endec;

import io.netty.buffer.ByteBuf;

import static com.example.greenapp.atmservice.Const.MAX_ATM_ID;
import static com.example.greenapp.atmservice.Const.MIN_ATM_ID;
import static com.example.greenapp.endec.Decoder.readNestedPositiveIntMax9x4;

public class AtmIdDecoder {

  static {
    if (MIN_ATM_ID != 1 || MAX_ATM_ID != 9_999) {
      throw new LinkageError("ATM ID decoder assumptions broken");
    }
  }

  public static int decodeAtmId(ByteBuf buf) {
    return readNestedPositiveIntMax9x4(buf);
  }

}
