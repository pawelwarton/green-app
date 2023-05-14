package com.example.greenapp.transactions.slow;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

import static com.example.greenapp.transactions.acc.AccountIdEncoder.encodeAccountId;
import static com.example.greenapp.transactions.endec.CreditCountEncoder.encodeCreditCount;
import static com.example.greenapp.transactions.endec.DebitCountEncoder.encodeDebitCount;

public class Encoder {

  public static void encode(Iterable<Account> accounts, ByteBuf buf) {
    buf.writeByte('[');
    var iter = accounts.iterator();
    if (iter.hasNext()) {
      writeAccount(iter.next(), buf);
      while (iter.hasNext()) {
        buf.writeByte(',');
        writeAccount(iter.next(), buf);
      }
    }
    buf.writeByte(']');
  }

  private static void writeAccount(Account account, ByteBuf buf) {
    buf.writeBytes(P1);
    encodeAccountId(account.getAccount(), buf);
    buf.writeBytes(P2);
    encodeDebitCount(account.getDebitCount(), buf);
    buf.writeBytes(P3);
    encodeCreditCount(account.getCreditCount(), buf);
    buf.writeBytes(P4);
    com.example.greenapp.endec.Encoder.encode(account.getBalance(), buf);
    buf.writeByte('}');
  }

  private static final byte[] P1 = "{\"account\":\"".getBytes(StandardCharsets.UTF_8);
  private static final byte[] P2 = "\",\"debitCount\":".getBytes(StandardCharsets.UTF_8);
  private static final byte[] P3 = ",\"creditCount\":".getBytes(StandardCharsets.UTF_8);
  private static final byte[] P4 = ",\"balance\":".getBytes(StandardCharsets.UTF_8);

}
