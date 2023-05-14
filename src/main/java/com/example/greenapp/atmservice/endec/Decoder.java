package com.example.greenapp.atmservice.endec;

import com.example.greenapp.BadRequestException;
import com.example.greenapp.atmservice.RequestType;
import com.example.greenapp.atmservice.Tasks;
import com.example.greenapp.endec.DuplicatedFieldException;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.atmservice.Const.MIN_ATM_ID;
import static com.example.greenapp.atmservice.Const.MIN_REGION;
import static com.example.greenapp.atmservice.endec.AtmIdDecoder.decodeAtmId;
import static com.example.greenapp.atmservice.endec.RegionDecoder.decodeRegion;
import static com.example.greenapp.atmservice.endec.RequestTypeDecoder.decodeRequestType;
import static com.example.greenapp.endec.Decoder.*;

public class Decoder {
  private static final int NO_REGION = 0;
  private static final int NO_ATM_ID = 0;

  static {
    if (MIN_REGION == 0 || MIN_ATM_ID == 0) {
      throw new LinkageError("Tasks decoder assumptions broken");
    }
  }

  /**
   * Features:
   * - list of transactions cannot be null
   * - list of tarnsactions can be empty
   * - object cannot be empty
   * - no duplicate fields
   */
  public static void decode(ByteBuf buf, Tasks tasks) {
    readToArrayStart(buf);
    for (; ; ) {
      if (!tasks.isEmpty()) {
        if (readToCommaOrArrayEnd(buf) == ']') {
          break;
        }
      }
      if (readToObjectStartOrArrayEnd(buf) == ']') {
        break;
      }
      int region = NO_REGION;
      RequestType requestType = null;
      int atmId = NO_ATM_ID;
      do {
        readToStringStart(buf);
        // TODO: vectorize
        switch (buf.readByte()) {
          case 'r' -> {
            if (buf.readByte() != 'e') {
              throw new BadRequestException("Unknown field");
            }
            switch (buf.readByte()) {
              case 'g' -> {
                if (buf.readByte() != 'i'
                  || buf.readByte() != 'o'
                  || buf.readByte() != 'n'
                  || buf.readByte() != '"'
                ) {
                  throw new BadRequestException("Unknown field");
                }
                if (region != 0) {
                  throw new DuplicatedFieldException("region");
                }
                readToColon(buf);
                region = decodeRegion(buf);
              }
              case 'q' -> {
                if (buf.readByte() != 'u'
                  || buf.readByte() != 'e'
                  || buf.readByte() != 's'
                  || buf.readByte() != 't'
                  || buf.readByte() != 'T'
                  || buf.readByte() != 'y'
                  || buf.readByte() != 'p'
                  || buf.readByte() != 'e'
                  || buf.readByte() != '"'
                ) {
                  throw new BadRequestException("Uknown field");
                }
                if (requestType != null) {
                  throw new DuplicatedFieldException("requestType");
                }
                readToColon(buf);
                requestType = decodeRequestType(buf);
              }
              default -> throw new BadRequestException("Unknown field");
            }
          }
          case 'a' -> {
            if (buf.readByte() != 't'
              || buf.readByte() != 'm'
              || buf.readByte() != 'I'
              || buf.readByte() != 'd'
              || buf.readByte() != '"'
            ) {
              throw new BadRequestException("Unknown field");
            }
            if (atmId != 0) {
              throw new DuplicatedFieldException("atmId");
            }
            readToColon(buf);
            atmId = decodeAtmId(buf);
          }
          default -> throw new BadRequestException("Unknown field");
        }
      } while (readToCommaOrObjectEnd(buf) == ',');
      if (region == NO_REGION || requestType == null || atmId == NO_ATM_ID) {
        throw new BadRequestException("Missing required fields");
      }
      tasks.add(region, requestType, atmId);
    }
    readUntilEnd(buf);
  }

}
