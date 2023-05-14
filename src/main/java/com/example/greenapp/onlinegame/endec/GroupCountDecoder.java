package com.example.greenapp.onlinegame.endec;

import com.example.greenapp.BadRequestException;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Decoder.isAsciiDigit;
import static com.example.greenapp.endec.Decoder.isAsciiDigitExceptZero;
import static com.example.greenapp.endec.Decoder.isWhitespace;
import static com.example.greenapp.onlinegame.Const.MAX_GROUP_SIZE;
import static com.example.greenapp.onlinegame.Const.MIN_GROUP_SIZE;

public class GroupCountDecoder {

  static {
    if (MIN_GROUP_SIZE < 1 || 9_999 < MAX_GROUP_SIZE) {
      throw new LinkageError("Group count decoder assumptions broken");
    }
  }

  public static int decodeGroupCount(ByteBuf buf) {
    var b1 = buf.readByte();
    while (!isAsciiDigitExceptZero(b1)) {
      if (!isWhitespace(b1)) {
        throw new BadRequestException("Invalid groupCount");
      }
      b1 = buf.readByte();
    }
    var b2 = buf.readByte();
    if (!isAsciiDigit(b2)) {
      buf.readerIndex(buf.readerIndex() - 1);
      return b1 - '0';
    }
    if (b1 == '0') {
      throw new BadRequestException("Invalid groupCount");
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
    var result = (b1 - '0') * 1_000 + (b2 - '0') * 100 + (b3 - '0') * 10 + (b4 - '0');
    if (result > MAX_GROUP_SIZE) {
      throw new BadRequestException("Invalid groupCount");
    }
    return result;
  }

}
