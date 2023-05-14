package com.example.greenapp.atmservice.endec;

import io.netty.buffer.ByteBuf;

import static com.example.greenapp.atmservice.Const.MAX_REGION;
import static com.example.greenapp.atmservice.Const.MIN_REGION;
import static com.example.greenapp.endec.Decoder.readNestedPositiveIntMax9x4;

public class RegionDecoder {

  static {
    if (MIN_REGION != 1 || MAX_REGION != 9_999) {
      throw new LinkageError("Region decoder assumptions broken");
    }
  }

  public static int decodeRegion(ByteBuf buf) {
    return readNestedPositiveIntMax9x4(buf);
  }

}
