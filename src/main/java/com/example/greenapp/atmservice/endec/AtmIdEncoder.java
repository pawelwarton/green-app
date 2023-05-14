package com.example.greenapp.atmservice.endec;

import io.netty.buffer.ByteBuf;

import static com.example.greenapp.atmservice.Const.MAX_ATM_ID;
import static com.example.greenapp.atmservice.Const.MIN_ATM_ID;
import static com.example.greenapp.endec.Encoder.encodeUnsignedMax9x4;

public class AtmIdEncoder {

  static {
    if (MIN_ATM_ID < 0 || 9_999 < MAX_ATM_ID) {
      throw new LinkageError("ATM ID encoder assumptions broken");
    }
  }

  public static void encodeAtmId(int atmId, ByteBuf buf) {
    encodeUnsignedMax9x4(atmId, buf);
  }

}
