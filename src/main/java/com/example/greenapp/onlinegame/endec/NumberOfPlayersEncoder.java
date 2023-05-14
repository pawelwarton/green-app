package com.example.greenapp.onlinegame.endec;

import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Encoder.encodeUnsignedMax9x4;
import static com.example.greenapp.onlinegame.Const.MAX_PLAYER_COUNT;
import static com.example.greenapp.onlinegame.Const.MIN_PLAYER_COUNT;

public class NumberOfPlayersEncoder {
  static {
    if (MIN_PLAYER_COUNT < 0 || 9_999 < MAX_PLAYER_COUNT) {
      throw new LinkageError("Number of players encoder assumptions broken");
    }
  }

  public static void encodeNumberOfPlayers(int value, ByteBuf buf) {
    encodeUnsignedMax9x4(value, buf);
  }
}
