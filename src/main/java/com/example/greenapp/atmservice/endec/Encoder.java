package com.example.greenapp.atmservice.endec;

import com.example.greenapp.atmservice.ATM;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.atmservice.endec.AtmIdEncoder.encodeAtmId;
import static com.example.greenapp.atmservice.endec.RegionEncoder.encodeRegion;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Encoder {

  public static void encode(Iterable<? extends ATM> atms, ByteBuf buf) {
    buf.writeByte('[');
    var iter = atms.iterator();
    if (iter.hasNext()) {
      writeATM(iter.next(), buf);
      while (iter.hasNext()) {
        buf.writeByte(',');
        writeATM(iter.next(), buf);
      }
    }
    buf.writeByte(']');
  }

  private static final byte[] P1 = "{\"region\":".getBytes(UTF_8);
  private static final byte[] P2 = ",\"atmId\":".getBytes(UTF_8);

  private static void writeATM(ATM atm, ByteBuf buf) {
    buf.writeBytes(P1);
    encodeRegion(atm.getRegion(), buf);
    buf.writeBytes(P2);
    encodeAtmId(atm.getAtmId(), buf);
    buf.writeByte('}');
  }

}
