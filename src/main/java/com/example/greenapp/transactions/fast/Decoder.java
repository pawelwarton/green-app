package com.example.greenapp.transactions.fast;

import com.example.greenapp.BadRequestException;
import com.example.greenapp.endec.DuplicatedFieldException;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Decoder.*;
import static com.example.greenapp.transactions.Const.ACCOUNT_LEN;
import static com.example.greenapp.transactions.acc.AccountIdDecoder.decodeAccountIdFieldValue;
import static com.example.greenapp.transactions.endec.PolishZlotyDecoder.decodePolishZlotyFieldValueAsLong;

public class Decoder {
  static {
    if (ACCOUNT_LEN != 26) {
      throw new LinkageError();
    }
  }

  /**
   * Features:
   * - list of transactions cannot be null
   * - list of tarnsactions can be empty
   * - no missing fields
   * - no duplicate fields
   */
  public static void decode(ByteBuf buf, Transactions transactions) {
    readToArrayStart(buf);
    for (; ; ) {
      if (!transactions.isEmpty()) {
        if (readToCommaOrArrayEnd(buf) == ']') {
          break;
        }
      }
      if (readToObjectStartOrArrayEnd(buf) == ']') {
        break;
      }
      var transaction = transactions.next();
      var creditAccountRead = false;
      var debitAccountRead = false;
      var amountRead = false;
      do {
        readToStringStart(buf);
        // Fields:
        // - debitAccount
        // - creditAccount
        // - amount
        // TODO: vectorize
        switch (buf.readByte()) {
          case 'd' -> {
            if (
              buf.readByte() != 'e'
                || buf.readByte() != 'b'
                || buf.readByte() != 'i'
                || buf.readByte() != 't'
                || buf.readByte() != 'A'
                || buf.readByte() != 'c'
                || buf.readByte() != 'c'
                || buf.readByte() != 'o'
                || buf.readByte() != 'u'
                || buf.readByte() != 'n'
                || buf.readByte() != 't'
                || buf.readByte() != '"'
            ) {
              throw new BadRequestException("Unknown field");
            }
            if (debitAccountRead) {
              throw new DuplicatedFieldException("debitAccount");
            }
            readToColon(buf);
            decodeAccountIdFieldValue(buf, transaction.getDebitAccount());
            debitAccountRead = true;
          }
          case 'c' -> {
            if (
              buf.readByte() != 'r'
                || buf.readByte() != 'e'
                || buf.readByte() != 'd'
                || buf.readByte() != 'i'
                || buf.readByte() != 't'
                || buf.readByte() != 'A'
                || buf.readByte() != 'c'
                || buf.readByte() != 'c'
                || buf.readByte() != 'o'
                || buf.readByte() != 'u'
                || buf.readByte() != 'n'
                || buf.readByte() != 't'
                || buf.readByte() != '"'
            ) {
              throw new BadRequestException("Unknown field");
            }
            if (creditAccountRead) {
              throw new DuplicatedFieldException("creditAccount");
            }
            readToColon(buf);
            decodeAccountIdFieldValue(buf, transaction.getCreditAccount());
            creditAccountRead = true;
          }
          case 'a' -> {
            if (
              buf.readByte() != 'm'
                || buf.readByte() != 'o'
                || buf.readByte() != 'u'
                || buf.readByte() != 'n'
                || buf.readByte() != 't'
                || buf.readByte() != '"'
            ) {
              throw new BadRequestException("Unknown field");
            }
            if (amountRead) {
              throw new DuplicatedFieldException("amount");
            }
            readToColon(buf);
            transaction.setAmount(decodePolishZlotyFieldValueAsLong(buf));
            amountRead = true;
          }
          default -> throw new BadRequestException("Unknown field");
        }
      } while (readToCommaOrObjectEnd(buf) == ',');
      if (!debitAccountRead || !creditAccountRead || !amountRead) {
        throw new BadRequestException("Missing required fields");
      }
    }
    readUntilEnd(buf);
  }

}
