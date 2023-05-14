package com.example.greenapp.onlinegame.endec;

import com.example.greenapp.BadRequestException;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Decoder.isAsciiDigit;
import static com.example.greenapp.endec.Decoder.isAsciiDigitExceptZero;
import static com.example.greenapp.endec.Decoder.isWhitespace;
import static com.example.greenapp.onlinegame.Const.MAX_POINTS;
import static com.example.greenapp.onlinegame.Const.MIN_POINTS;

public class PointsDecoder {

  static {
    if (MIN_POINTS < 1 || 9_999_999 < MAX_POINTS) {
      throw new LinkageError("Points decoder assumptions broken");
    }
  }

  public static int decodePoints(ByteBuf buf) {
    var b1 = buf.readByte();
    while (!isAsciiDigitExceptZero(b1)) {
      if (!isWhitespace(b1)) {
        throw new BadRequestException("Invalid points");
      }
      b1 = buf.readByte();
    }
    var b2 = buf.readByte();
    if (!isAsciiDigit(b2)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return b1 - '0';
    }
    if (b1 == '0') {
      throw new BadRequestException("Invalid points");
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
    var b5 = buf.readByte();
    if (!isAsciiDigit(b5)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return (b1 - '0') * 1_000 + (b2 - '0') * 100 + (b3 - '0') * 10 + (b4 - '0');
    }
    var b6 = buf.readByte();
    if (!isAsciiDigit(b6)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return (b1 - '0') * 10_000 + (b2 - '0') * 1_000 + (b3 - '0') * 100 + (b4 - '0') * 10 + (b5 - '0');
    }
    var b7 = buf.readByte();
    if (!isAsciiDigit(b7)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return (b1 - '0') * 100_000 + (b2 - '0') * 10_000 + (b3 - '0') * 1_000 + (b4 - '0') * 100 + (b5 - '0') * 10 + (b6 - '0');
    }
    var result = (b1 - '0') * 1_000_000 + (b2 - '0') * 100_000 + (b3 - '0') * 10_000 + (b4 - '0') * 1_000 + (b5 - '0') * 100 + (b6 - '0') * 10 + (b7 - '0');
    if (result > MAX_POINTS) {
      throw new BadRequestException("Invalid points");
    }
    return result;
  }

}
