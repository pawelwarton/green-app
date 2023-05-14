package com.example.greenapp.onlinegame.endec;

import com.example.greenapp.onlinegame.Clan;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static com.example.greenapp.onlinegame.endec.NumberOfPlayersEncoder.encodeNumberOfPlayers;
import static com.example.greenapp.onlinegame.endec.PointsEncoder.encodePoints;

public class Encoder {

  public static void encode(Collection<? extends Collection<? extends Clan>> groups, ByteBuf buf) {
    buf.writeByte('[');
    var iter = groups.iterator();
    if (iter.hasNext()) {
      writeGroup(iter.next(), buf);
      while (iter.hasNext()) {
        buf.writeByte(',');
        writeGroup(iter.next(), buf);
      }
    }
    buf.writeByte(']');
  }

  private static void writeGroup(Collection<? extends Clan> group, ByteBuf buf) {
    buf.writeByte('[');
    var iter = group.iterator();
    if (iter.hasNext()) {
      writeClan(iter.next(), buf);
      while (iter.hasNext()) {
        buf.writeByte(',');
        writeClan(iter.next(), buf);
      }
    }
    buf.writeByte(']');
  }

  private static final byte[] P1 = "{\"numberOfPlayers\":".getBytes(StandardCharsets.UTF_8);
  private static final byte[] P2 = ",\"points\":".getBytes(StandardCharsets.UTF_8);

  private static void writeClan(Clan clan, ByteBuf buf) {
    buf.writeBytes(P1);
    encodeNumberOfPlayers(clan.getNumberOfPlayers(), buf);
    buf.writeBytes(P2);
    encodePoints(clan.getPoints(), buf);
    buf.writeByte('}');
  }

}
