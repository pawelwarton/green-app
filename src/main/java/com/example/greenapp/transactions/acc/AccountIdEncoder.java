package com.example.greenapp.transactions.acc;

import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Encoder.encode18Digits;
import static com.example.greenapp.endec.Encoder.encode8Digits;

public class AccountIdEncoder {

  public static void encodeAccountId(AccountId account, ByteBuf buf) {
    encode18Digits(account.left, buf);
    encode8Digits(account.right, buf);
  }
}
