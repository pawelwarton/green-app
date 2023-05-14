package com.example.greenapp.atmservice.endec;

import io.netty.buffer.ByteBuf;

import static com.example.greenapp.atmservice.Const.MAX_REGION;
import static com.example.greenapp.atmservice.Const.MIN_REGION;
import static com.example.greenapp.endec.Encoder.encodeUnsignedMax9x4;

public class RegionEncoder {

  static {
    if (MIN_REGION < 0 || 9_999 < MAX_REGION) {
      throw new LinkageError("Region encoder assumptions broken");
    }
  }

  public static void encodeRegion(int region, ByteBuf buf) {
    encodeUnsignedMax9x4(region, buf);
  }

}
