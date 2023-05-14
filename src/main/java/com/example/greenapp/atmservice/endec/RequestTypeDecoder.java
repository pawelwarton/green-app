package com.example.greenapp.atmservice.endec;

import com.example.greenapp.BadRequestException;
import com.example.greenapp.atmservice.RequestType;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.endec.Decoder.readToStringStart;

public class RequestTypeDecoder {

  public static RequestType decodeRequestType(ByteBuf buf) {
    readToStringStart(buf);
    switch (buf.readByte()) {
      // TODO: vectorize
      case 'F' -> {
        if (
          buf.readByte() != 'A'
            || buf.readByte() != 'I'
            || buf.readByte() != 'L'
            || buf.readByte() != 'U'
            || buf.readByte() != 'R'
            || buf.readByte() != 'E'
            || buf.readByte() != '_'
            || buf.readByte() != 'R'
            || buf.readByte() != 'E'
            || buf.readByte() != 'S'
            || buf.readByte() != 'T'
            || buf.readByte() != 'A'
            || buf.readByte() != 'R'
            || buf.readByte() != 'T'
            || buf.readByte() != '"'
        ) {
          throw new BadRequestException("Invalid requestType");
        }
        return RequestType.FAILURE_RESTART;
      }
      case 'P' -> {
        if (
          buf.readByte() != 'R'
            || buf.readByte() != 'I'
            || buf.readByte() != 'O'
            || buf.readByte() != 'R'
            || buf.readByte() != 'I'
            || buf.readByte() != 'T'
            || buf.readByte() != 'Y'
            || buf.readByte() != '"'
        ) {
          throw new BadRequestException("Invalid requestType");
        }
        return RequestType.PRIORITY;
      }
      case 'S' -> {
        switch (buf.readByte()) {
          case 'I' -> {
            if (
              buf.readByte() != 'G'
                || buf.readByte() != 'N'
                || buf.readByte() != 'A'
                || buf.readByte() != 'L'
                || buf.readByte() != '_'
                || buf.readByte() != 'L'
                || buf.readByte() != 'O'
                || buf.readByte() != 'W'
                || buf.readByte() != '"'
            ) {
              throw new BadRequestException("Invalid requestType");
            }
            return RequestType.SIGNAL_LOW;
          }
          case 'T' -> {
            if (
              buf.readByte() != 'A'
                || buf.readByte() != 'N'
                || buf.readByte() != 'D'
                || buf.readByte() != 'A'
                || buf.readByte() != 'R'
                || buf.readByte() != 'D'
                || buf.readByte() != '"'
            ) {
              throw new BadRequestException("Invalid requestType");
            }
            return RequestType.STANDARD;
          }
          default -> throw new BadRequestException("Invalid requestType");
        }
      }
      default -> throw new BadRequestException("Invalid requestType");
    }
  }

}
