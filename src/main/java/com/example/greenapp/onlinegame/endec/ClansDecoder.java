package com.example.greenapp.onlinegame.endec;

import com.example.greenapp.BadRequestException;
import com.example.greenapp.endec.DuplicatedFieldException;
import com.example.greenapp.onlinegame.Clans;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Decoder.*;
import static com.example.greenapp.onlinegame.Const.MIN_PLAYER_COUNT;
import static com.example.greenapp.onlinegame.Const.MIN_POINTS;
import static com.example.greenapp.onlinegame.endec.NumberOfPlayersDecoder.decodeNumberOfPlayers;
import static com.example.greenapp.onlinegame.endec.PointsDecoder.decodePoints;

public class ClansDecoder {
  private static final int NO_NUMBER_OF_PLAYERS = 0;
  private static final int NO_POINTS = 0;
  static {
    if (MIN_PLAYER_COUNT == 0 || MIN_POINTS == 0) {
      throw new LinkageError("Clans decoder assumptions broken");
    }
  }

  public static void readClans(ByteBuf buf, Clans clans) {
    readToArrayStart(buf);
    for (; ; ) {
      if (!clans.isEmpty()) {
        if (readToCommaOrArrayEnd(buf) == ']') {
          break;
        }
      }
      if (readToObjectStartOrArrayEnd(buf) == ']') {
        break;
      }
      var numberOfPlayers = NO_NUMBER_OF_PLAYERS;
      var points = NO_POINTS;
      do {
        // TODO: vectorize
        readToStringStart(buf);
        switch (buf.readByte()) {
          case 'n' -> {
            if (buf.readByte() != 'u'
              || buf.readByte() != 'm'
              || buf.readByte() != 'b'
              || buf.readByte() != 'e'
              || buf.readByte() != 'r'
              || buf.readByte() != 'O'
              || buf.readByte() != 'f'
              || buf.readByte() != 'P'
              || buf.readByte() != 'l'
              || buf.readByte() != 'a'
              || buf.readByte() != 'y'
              || buf.readByte() != 'e'
              || buf.readByte() != 'r'
              || buf.readByte() != 's'
              || buf.readByte() != '"'
            ) {
              throw new BadRequestException("Unknown field");
            }
            if (numberOfPlayers != NO_NUMBER_OF_PLAYERS) {
              throw new DuplicatedFieldException("numberOfPlayers");
            }
            readToColon(buf);
            numberOfPlayers = decodeNumberOfPlayers(buf);
          }
          case 'p' -> {
            if (buf.readByte() != 'o'
              || buf.readByte() != 'i'
              || buf.readByte() != 'n'
              || buf.readByte() != 't'
              || buf.readByte() != 's'
              || buf.readByte() != '"'
            ) {
              throw new BadRequestException("Unknown field");
            }
            if (points != NO_POINTS) {
              throw new DuplicatedFieldException("points");
            }
            readToColon(buf);
            points = decodePoints(buf);
          }
          default -> throw new BadRequestException("Unknown field");
        }
      } while (readToCommaOrObjectEnd(buf) == ',');
      if (numberOfPlayers == NO_NUMBER_OF_PLAYERS || points == NO_POINTS) {
        throw new BadRequestException("Missing required fields");
      }
      clans.add(numberOfPlayers, points);
    }
  }

}
