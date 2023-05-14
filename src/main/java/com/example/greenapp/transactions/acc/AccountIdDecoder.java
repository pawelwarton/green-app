package com.example.greenapp.transactions.acc;

import com.example.greenapp.BadRequestException;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Decoder.isAsciiDigit;
import static com.example.greenapp.endec.Decoder.readToStringStart;
import static com.example.greenapp.transactions.acc.AccountId.LEFT_DIGIT_COUNT;
import static com.example.greenapp.transactions.acc.AccountId.RIGHT_DIGIT_COUNT;

public class AccountIdDecoder {

  static {
    if (LEFT_DIGIT_COUNT != 18 || RIGHT_DIGIT_COUNT != 8) {
      throw new LinkageError("Account ID decoder assumptions broken");
    }
  }

  /**
   * Features:
   * - must be the value of a field
   * - null is invalid
   */
  public static void decodeAccountIdFieldValue(ByteBuf buf, AccountId account) {
    readToStringStart(buf);
    var x1 = buf.readLong(); // 8 digits
    var x2 = buf.readLong(); // 8 digits
    var x3 = buf.readLong(); // 8 digits
    var x4 = buf.readShort(); // 2 digits
    var x5 = buf.readByte(); // `"`
    var b01 = (byte) (x1 >>> 56);
    var b02 = (byte) (x1 >>> 48);
    var b03 = (byte) (x1 >>> 40);
    var b04 = (byte) (x1 >>> 32);
    var b05 = (byte) (x1 >>> 24);
    var b06 = (byte) (x1 >>> 16);
    var b07 = (byte) (x1 >>> 8);
    var b08 = (byte) (x1);
    var b09 = (byte) (x2 >>> 56);
    var b10 = (byte) (x2 >>> 48);
    var b11 = (byte) (x2 >>> 40);
    var b12 = (byte) (x2 >>> 32);
    var b13 = (byte) (x2 >>> 24);
    var b14 = (byte) (x2 >>> 16);
    var b15 = (byte) (x2 >>> 8);
    var b16 = (byte) (x2);
    var b17 = (byte) (x3 >>> 56);
    var b18 = (byte) (x3 >>> 48);
    var b19 = (byte) (x3 >>> 40);
    var b20 = (byte) (x3 >>> 32);
    var b21 = (byte) (x3 >>> 24);
    var b22 = (byte) (x3 >>> 16);
    var b23 = (byte) (x3 >>> 8);
    var b24 = (byte) (x3);
    var b25 = (byte) (x4 >>> 8);
    var b26 = (byte) (x4);
    if (
      !isAsciiDigit(b01)
        || !isAsciiDigit(b02)
        || !isAsciiDigit(b03)
        || !isAsciiDigit(b04)
        || !isAsciiDigit(b05)
        || !isAsciiDigit(b06)
        || !isAsciiDigit(b07)
        || !isAsciiDigit(b08)
        || !isAsciiDigit(b09)
        || !isAsciiDigit(b10)
        || !isAsciiDigit(b11)
        || !isAsciiDigit(b12)
        || !isAsciiDigit(b13)
        || !isAsciiDigit(b14)
        || !isAsciiDigit(b15)
        || !isAsciiDigit(b16)
        || !isAsciiDigit(b17)
        || !isAsciiDigit(b18)
        || !isAsciiDigit(b19)
        || !isAsciiDigit(b20)
        || !isAsciiDigit(b21)
        || !isAsciiDigit(b22)
        || !isAsciiDigit(b23)
        || !isAsciiDigit(b24)
        || !isAsciiDigit(b25)
        || !isAsciiDigit(b26)
        || x5 != '"'
    ) {
      decodeAccountIdFieldValueWithJsonEscapes(buf.readerIndex(buf.readerIndex() - 27), account);
    } else {
      account.left = -5_333_333_333_333_333_328L // precomputed `- '0'` stuff
        + (b01 * 100_000_000_000_000_000L)
        + (b02 * 10_000_000_000_000_000L)
        + (b03 * 1_000_000_000_000_000L)
        + (b04 * 100_000_000_000_000L)
        + (b05 * 10_000_000_000_000L)
        + (b06 * 1_000_000_000_000L)
        + (b07 * 100_000_000_000L)
        + (b08 * 10_000_000_000L)
        + (b09 * 1_000_000_000L)
        + (b10 * 100_000_000L)
        + (b11 * 10_000_000L)
        + (b12 * 1_000_000L)
        + (b13 * 100_000L)
        + (b14 * 10_000L)
        + (b15 * 1_000L)
        + (b16 * 100L)
        + (b17 * 10L)
        + (b18);
      account.right = -533_333_328 // precomputed `- '0'` stuff
        + (b19 * 10_000_000)
        + (b20 * 1_000_000)
        + (b21 * 100_000)
        + (b22 * 10_000)
        + (b23 * 1_000)
        + (b24 * 100)
        + (b25 * 10)
        + (b26);
    }
  }

  private static void decodeAccountIdFieldValueWithJsonEscapes(ByteBuf buf, AccountId account) {
    // We don't really care about performance here and in the subroutines
    //
    // escape
    //   'u' hex hex hex hex
    var left = 0L;
    var leftMul = 100_000_000_000_000_000L;
    for (int i = 0; i < LEFT_DIGIT_COUNT; i++) {
      int digit;
      var b = buf.readByte();
      if (isAsciiDigit(b)) {
        digit = b - '0';
      } else if (b == '\\') {
        digit = decodeEscapeAfterBackslash(buf) - '0';
      } else {
        throw new BadRequestException("Invalid account");
      }
      left += digit * leftMul;
      leftMul /= 10;
    }
    var right = 0;
    var rightMul = 10_000_000;
    for (int i = 0; i < RIGHT_DIGIT_COUNT; i++) {
      int digit;
      var b = buf.readByte();
      if (isAsciiDigit(b)) {
        digit = b - '0';
      } else if (b == '\\') {
        digit = decodeEscapeAfterBackslash(buf) - '0';
      } else {
        throw new BadRequestException("Invalid account");
      }
      right += digit * rightMul;
      rightMul /= 10;
    }
    if (buf.readByte() != '"') {
      throw new BadRequestException("Invalid account");
    }
    account.left = left;
    account.right = right;
  }

  private static int decodeEscapeAfterBackslash(ByteBuf buf) {
    // ", \, /, b, f, n, r, t escapes are not allowed
    if (buf.readByte() == 'u') {
      var escape = decodeHexNumber4(buf.readInt());
      if ('0' <= escape && escape <= '9') {
        return escape;
      }
    }
    throw new BadRequestException("Invalid account");
  }

  private static int decodeHexNumber4(int i) {
    return 16 * 16 * 16 * decodeHexDigit((byte) (i >>> 24))
      + 16 * 16 * decodeHexDigit((byte) (i >>> 16))
      + 16 * decodeHexDigit((byte) (i >>> 8))
      + decodeHexDigit((byte) i);
  }

  private static int decodeHexDigit(byte b) {
    if ('0' <= b && b <= '9') {
      return b - '0';
    }
    if ('a' <= b && b <= 'f') {
      return b - 'a' + 10;
    }
    if ('A' <= b && b <= 'F') {
      return b - 'A' + 10;
    }
    throw new BadRequestException("Invalid account");
  }

}
