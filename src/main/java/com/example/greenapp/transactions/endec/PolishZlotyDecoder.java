package com.example.greenapp.transactions.endec;

import com.example.greenapp.OptimizationAssumptionException;
import io.netty.buffer.ByteBuf;

import java.math.BigDecimal;
import java.util.Arrays;

public class PolishZlotyDecoder {
  private static final long MAX_POLISH_ZLOTY_INTEGER = 9_999_999_999_999_999L;

  /**
   * Features:
   * - must be the value of a field
   * - null is invalid
   * - fractions of a grosz are not allowed
   */
  public static long decodePolishZlotyFieldValueAsLong(ByteBuf buf) {
    whitespace:
    for (; ; ) {
      switch (buf.readByte()) {
        case ' ':
        case '\n':
        case '\r':
        case '\t':
          break;
        default:
          buf.readerIndex(buf.readerIndex() - 1);
          break whitespace;
      }
    }
    var startReaderIndex = buf.readerIndex();

    enum State {
      READING_INTEGER,
      READING_FRACTION,
    }
    var state = State.READING_INTEGER;
    var integer = 0L;
    var b = buf.readByte();
    switch (b) {
      case '0' -> {
        switch (buf.readByte()) {
          // should opitmize for '.'
          case ' ', '\n', '\r', '\t' -> {
            return 0;
          }
          case ',', '}' -> {
            buf.readerIndex(buf.readerIndex() - 1);
            return 0;
          }
          case '.' -> state = State.READING_FRACTION;
          case 'e', 'E' -> {
            // this will be 0, but we need to consume remaining bytes
            return readPolishZlotyFieldValueAsLongSlow(buf.readerIndex(startReaderIndex));
          }
          default -> throw new InvalidTransactionAmount("Leading zero");
        }
      }
      case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> integer = b - '0';
      default -> {
        if (b == '-') {
          // negative zero or an error
          return readPolishZlotyFieldValueAsLongSlow(buf.readerIndex(startReaderIndex));
        }
        throw new InvalidTransactionAmount();
      }
    }
    while (state == State.READING_INTEGER) {
      b = buf.readByte();
      switch (b) {
        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
          if ((integer = 10 * integer + b - '0') > MAX_POLISH_ZLOTY_INTEGER) {
            throw new OptimizationAssumptionException();
          }
        }
        case 'e', 'E' -> {
          return readPolishZlotyFieldValueAsLongSlow(buf.readerIndex(startReaderIndex));
        }
        case '.' -> state = State.READING_FRACTION;
        default -> {
          buf.readerIndex(buf.readerIndex() - 1);
          return integer * 100;
        }
      }
    }
    b = buf.readByte();
    long fraction = switch (b) {
      case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> b - '0';
      default ->
        // trailing dot is illegal
        throw new InvalidTransactionAmount();
    };
    b = buf.readByte();
    switch (b) {
      case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> fraction = 10 * fraction + b - '0';
      case 'e', 'E' -> {
        return readPolishZlotyFieldValueAsLongSlow(buf.readerIndex(startReaderIndex));
      }
      default -> {
        buf.readerIndex(buf.readerIndex() - 1);
        return 100 * integer + 10 * fraction;
      }
    }
    b = buf.readByte();
    switch (b) {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        break;
      case 'e':
      case 'E':
        return readPolishZlotyFieldValueAsLongSlow(buf.readerIndex(startReaderIndex));
      default:
        buf.readerIndex(buf.readerIndex() - 1);
        return 100 * integer + fraction;
    }
    return readPolishZlotyFieldValueAsLongSlow(buf.readerIndex(startReaderIndex));
  }

  private static long readPolishZlotyFieldValueAsLongSlow(ByteBuf buf) {
    var chars = new char[8];
    var i = 0;
    loop:
    for (; ; ) {
      var b = buf.readByte();
      switch (b) {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case '.':
        case '-':
        case '+':
        case 'e':
        case 'E':
          if (chars.length < i + 1) {
            chars = Arrays.copyOf(chars, chars.length * 2);
          }
          chars[i++] = (char) b;
          break;
        case '}':
        case ',':
          buf.readerIndex(buf.readerIndex() - 1);
          break loop;
        case ' ':
        case '\n':
        case '\r':
        case '\t':
          if (i == 0) {
            // leading whitespace
            break;
          } else {
            // trailing whitespace
            break loop;
          }
        default:
          throw new InvalidTransactionAmount();
      }
    }
    BigDecimal amountBigDecimal;
    try {
      amountBigDecimal = new BigDecimal(chars, 0, i);
    } catch (NumberFormatException e) {
      throw new InvalidTransactionAmount(e);
    }
    amountBigDecimal = amountBigDecimal.movePointRight(2);
    long amount;
    try {
      amount = amountBigDecimal.longValueExact();
    } catch (ArithmeticException e) {
      if (amountBigDecimal.stripTrailingZeros().scale() > 2) {
        throw new InvalidTransactionAmount("Transaction amount cannot have scale larger than 2", e);
      } else {
        throw new OptimizationAssumptionException(e);
      }
    }
    if (amount < 0) {
      throw new InvalidTransactionAmount("Transaction amount cannot be negative");
    }
    return amount;
  }

  public static BigDecimal decodePolishZlotyFieldValueAsBigDecimal(ByteBuf buf) {
    var chars = new char[8];
    var i = 0;
    loop:
    for (; ; ) {
      var b = buf.readByte();
      switch (b) {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case '.':
        case '-':
        case '+':
        case 'e':
        case 'E':
          if (chars.length < i + 1) {
            chars = Arrays.copyOf(chars, chars.length * 2);
          }
          chars[i++] = (char) b;
          break;
        case '}':
        case ',':
          buf.readerIndex(buf.readerIndex() - 1);
          break loop;
        case ' ':
        case '\n':
        case '\r':
        case '\t':
          if (i == 0) {
            // leading whitespace
            break;
          } else {
            // trailing whitespace
            break loop;
          }
        default:
          throw new InvalidTransactionAmount();
      }
    }
    BigDecimal amount;
    try {
      amount = new BigDecimal(chars, 0, i).stripTrailingZeros();
    } catch (NumberFormatException e) {
      throw new InvalidTransactionAmount(e);
    }
    if (amount.scale() > 2) {
      throw new InvalidTransactionAmount("Transaction amount cannot have scale larger than 2");
    }
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidTransactionAmount("Transaction amount cannot be negative");
    }
    return amount;
  }

}
