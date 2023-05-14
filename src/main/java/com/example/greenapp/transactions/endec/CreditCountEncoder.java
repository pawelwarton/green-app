package com.example.greenapp.transactions.endec;

import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Encoder.encodeUnsignedMax9x6;
import static com.example.greenapp.transactions.Const.MAX_TRANSACTION_COUNT;
import static com.example.greenapp.transactions.Const.MIN_TRANSACTION_COUNT;

public class CreditCountEncoder {

  static {
    if (MIN_TRANSACTION_COUNT < 0 || 999_999 < MAX_TRANSACTION_COUNT) {
      throw new LinkageError("Credit count encoder assumptions broken");
    }
  }

  public static void encodeCreditCount(int value, ByteBuf buf) {
    encodeUnsignedMax9x6(value, buf);
  }
}
