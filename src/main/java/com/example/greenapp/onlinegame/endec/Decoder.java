package com.example.greenapp.onlinegame.endec;

import com.example.greenapp.BadRequestException;
import com.example.greenapp.endec.DuplicatedFieldException;
import com.example.greenapp.onlinegame.Players;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Decoder.*;
import static com.example.greenapp.onlinegame.endec.ClansDecoder.readClans;
import static com.example.greenapp.onlinegame.endec.GroupCountDecoder.decodeGroupCount;

public class Decoder {

  public static void decode(ByteBuf buf, Players players) {
    readToObjectStart(buf);
    var groupCountRead = false;
    var clansRead = false;
    do {
      readToStringStart(buf);
      switch (buf.readByte()) {
        case 'g' -> {
          if (buf.readByte() != 'r'
            || buf.readByte() != 'o'
            || buf.readByte() != 'u'
            || buf.readByte() != 'p'
            || buf.readByte() != 'C'
            || buf.readByte() != 'o'
            || buf.readByte() != 'u'
            || buf.readByte() != 'n'
            || buf.readByte() != 't'
            || buf.readByte() != '"'
          ) {
            throw new BadRequestException("Unknown field");
          }
          if (groupCountRead) {
            throw new DuplicatedFieldException("groupCount");
          }
          readToColon(buf);
          players.setGroupCount(decodeGroupCount(buf));
          groupCountRead = true;
        }
        case 'c' -> {
          if (buf.readByte() != 'l'
            || buf.readByte() != 'a'
            || buf.readByte() != 'n'
            || buf.readByte() != 's'
            || buf.readByte() != '"'
          ) {
            throw new BadRequestException("Unknown field");
          }
          if (clansRead) {
            throw new DuplicatedFieldException("clans");
          }
          readToColon(buf);
          readClans(buf, players.getClans());
          clansRead = true;
        }
        default -> throw new BadRequestException("Unknown field");
      }
    } while (readToCommaOrObjectEnd(buf) == ',');
    if (!groupCountRead || !clansRead) {
      throw new BadRequestException("Missing required fields");
    }
    readUntilEnd(buf);
  }

}
