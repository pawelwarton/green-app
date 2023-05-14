package com.example.greenapp.endec;

import com.example.greenapp.BadRequestException;
import io.netty.buffer.ByteBuf;

public class Decoder {

  public static void readToArrayStart(ByteBuf buf) {
    for (; ; ) {
      switch (buf.readByte()) {
        case '[' -> {
          return;
        }
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new BadRequestException("Unexpected JSON");
      }
    }
  }

  public static void readToArrayStart2(ByteBuf buf) {
    if (buf.readByte() == '[') {
      return;
    }
    buf.readerIndex(buf.readerIndex() - 1);
    for (; ; ) {
      switch (buf.readByte()) {
        case '[' -> {
          return;
        }
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new BadRequestException("Unexpected JSON");
      }
    }
  }

  public static void readToObjectStart(ByteBuf buf) {
    for (; ; ) {
      switch (buf.readByte()) {
        case '{' -> {
          return;
        }
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new BadRequestException("Unexpected JSON");
      }
    }
  }

  public static void readToObjectStart2(ByteBuf buf) {
    if (buf.readByte() == '{') {
      return;
    }
    buf.readerIndex(buf.readerIndex() - 1);
    for (; ; ) {
      switch (buf.readByte()) {
        case '{' -> {
          return;
        }
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new BadRequestException("Unexpected JSON");
      }
    }
  }

  public static byte readToObjectStartOrArrayEnd(ByteBuf buf) {
    for (; ; ) {
      switch (buf.readByte()) {
        case '{' -> {
          return '{';
        }
        case ']' -> {
          return ']';
        }
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new BadRequestException("Unexpected JSON at index " + (buf.readerIndex() - 1)); // TODO: helper
      }
    }
  }

  public static byte readToCommaOrArrayEnd(ByteBuf buf) {
    for (; ; ) {
      switch (buf.readByte()) {
        case ',' -> {
          return ',';
        }
        case ']' -> {
          return ']';
        }
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new InvalidJsonException("Invalid JSON array");
      }
    }
  }

  public static void readToStringStart(ByteBuf buf) {
    if (buf.readByte() == '"') {
      return;
    }
    buf.readerIndex(buf.readerIndex() - 1);
    for (; ; ) {
      switch (buf.readByte()) {
        case '"' -> {
          return;
        }
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new BadRequestException("Unexpected JSON");
      }
    }
  }

  public static void readToColon(ByteBuf buf) {
    if (buf.readByte() == ':') {
      return;
    }
    buf.readerIndex(buf.readerIndex() - 1);
    for (; ; ) {
      switch (buf.readByte()) {
        case ':' -> {
          return;
        }
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new InvalidJsonException("Invalid JSON object");
      }
    }
  }

  public static byte readToCommaOrObjectEnd(ByteBuf buf) {
    for (; ; ) {
      switch (buf.readByte()) {
        case ',' -> {
          return ',';
        }
        case '}' -> {
          return '}';
        }
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new InvalidJsonException("Invalid JSON object");
      }
    }
  }

  public static void readUntilEnd(ByteBuf buf) {
    for (int i = 0, end = buf.readableBytes(); i < end; i++) {
      switch (buf.readByte()) {
        case ' ', '\n', '\r', '\t' -> {
        }
        default -> throw new InvalidJsonException("Trailing data");
      }
    }
  }

  public static int readNestedNonNegativeIntMax9x4(ByteBuf buf) {
    var b1 = buf.readByte();
    while (!isAsciiDigit(b1)) {
      if (!isWhitespace(b1)) {
        throw new BadRequestException("Invalid non-negative integer");
      }
      b1 = buf.readByte();
    }
    var b2 = buf.readByte();
    if (!isAsciiDigit(b2)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return b1 - '0';
    }
    if (b1 == '0') {
      throw new BadRequestException("Leading zero");
    }
    var b3 = buf.readByte();
    if (!isAsciiDigit(b3)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return (b1 - '0') * 10 + b2 - '0';
    }
    var b4 = buf.readByte();
    if (!isAsciiDigit(b4)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return (b1 - '0') * 100 + (b2 - '0') * 10 + (b3 - '0');
    }
    return (b1 - '0') * 1000 + (b2 - '0') * 100 + (b3 - '0') * 10 + (b4 - '0');
  }

  public static int readNestedPositiveIntMax9x4(ByteBuf buf) {
    var b1 = buf.readByte();
    while (!isAsciiDigitExceptZero(b1)) {
      if (!isWhitespace(b1)) {
        throw new BadRequestException("Invalid positive integer");
      }
      b1 = buf.readByte();
    }
    var b2 = buf.readByte();
    if (!isAsciiDigit(b2)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return b1 - '0';
    }
    if (b1 == '0') {
      throw new BadRequestException("Leading zero");
    }
    var b3 = buf.readByte();
    if (!isAsciiDigit(b3)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return (b1 - '0') * 10 + b2 - '0';
    }
    var b4 = buf.readByte();
    if (!isAsciiDigit(b4)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return (b1 - '0') * 100 + (b2 - '0') * 10 + (b3 - '0');
    }
    return (b1 - '0') * 1000 + (b2 - '0') * 100 + (b3 - '0') * 10 + (b4 - '0');
  }

  public static boolean isAsciiDigit(byte b) {
    return '0' <= b && b <= '9';
  }

  public static boolean isAsciiDigitExceptZero(byte b) {
    return '1' <= b && b <= '9';
  }

  public static boolean isWhitespace(byte b) {
    return b == ' ' || b == '\n' || b == '\r' || b == '\t';
  }

}
