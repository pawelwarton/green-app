package com.example.greenapp.transactions.endec;

import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Encoder.encode2DigitsAsShort;

public class PolishZlotyEncoder {

  private static final int MAX_LONG_LEN = 19;
  private static final int MAX_PLN_LEN = MAX_LONG_LEN - 2;

  public static void encodePolishZloty(long amount, ByteBuf buf) {
    if (amount < 0) {
      buf.writeByte('-');
      amount = -amount;
    }
    var zloty = amount / 100L;
    var grosz = (int) (amount - zloty * 100L);
    var startPos = buf.ensureWritable(MAX_PLN_LEN * 2).writerIndex() + MAX_PLN_LEN * 2;
    var pos = startPos;
    while (zloty >= 100L) {
      var q = zloty / 100L;
      var r = (int) (zloty - q * 100L);
      zloty = q;
      pos -= 2;
      buf.setShort(pos, encode2DigitsAsShort(r));
    }
    var zlotyInt = (int) zloty;
    if (zlotyInt < 10) {
      pos -= 1;
      buf.setByte(pos, zlotyInt + '0');
    } else {
      pos -= 2;
      buf.setShort(pos, encode2DigitsAsShort(zlotyInt));
    }
    buf.writeBytes(buf, pos, startPos - pos).writeByte('.').writeShort(encode2DigitsAsShort(grosz));
  }

}
