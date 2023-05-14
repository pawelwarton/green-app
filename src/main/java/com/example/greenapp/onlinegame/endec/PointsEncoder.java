package com.example.greenapp.onlinegame.endec;

import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Encoder.encodeUnsignedMax9x7;
import static com.example.greenapp.onlinegame.Const.MAX_POINTS;
import static com.example.greenapp.onlinegame.Const.MIN_POINTS;

public class PointsEncoder {
  static {
    if (MIN_POINTS < 0 || 9_999_999 < MAX_POINTS) {
      throw new LinkageError("Points encoder assumptions broken");
    }
  }

  public static void encodePoints(int value, ByteBuf buf) {
    encodeUnsignedMax9x7(value, buf);
  }

}
