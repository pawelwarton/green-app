package com.example.greenapp.endec;

import io.netty.buffer.ByteBuf;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class Encoder {
  private static final short[] DIGITS_100;

  static {
    DIGITS_100 = new short[100];
    for (int i = 0; i < 100; i++) {
      DIGITS_100[i] = (short) (((i / 10 + '0') << 8) + (i % 10 + '0'));
    }
  }

  public static void encode18Digits(long value, ByteBuf buf) {
    var l1 = value / 100;
    var l2 = l1 / 100;
    var l3 = l2 / 100;
    var l4 = l3 / 100;
    var l5 = l4 / 100;
    var l6 = l5 / 100;
    var l7 = l6 / 100;
    var l8 = l7 / 100;
    buf.writeShort(DIGITS_100[(int) l8]);
    buf.writeShort(DIGITS_100[(int) (l7 - l8 * 100)]);
    buf.writeShort(DIGITS_100[(int) (l6 - l7 * 100)]);
    buf.writeShort(DIGITS_100[(int) (l5 - l6 * 100)]);
    buf.writeShort(DIGITS_100[(int) (l4 - l5 * 100)]);
    buf.writeShort(DIGITS_100[(int) (l3 - l4 * 100)]);
    buf.writeShort(DIGITS_100[(int) (l2 - l3 * 100)]);
    buf.writeShort(DIGITS_100[(int) (l1 - l2 * 100)]);
    buf.writeShort(DIGITS_100[(int) (value - l1 * 100)]);
  }

  public static void encode8Digits(int value, ByteBuf buf) {
    var r1 = value / 100;
    var r2 = r1 / 100;
    var r3 = r2 / 100;
    buf.writeShort(DIGITS_100[r3]);
    buf.writeShort(DIGITS_100[r2 - r3 * 100]);
    buf.writeShort(DIGITS_100[r1 - r2 * 100]);
    buf.writeShort(DIGITS_100[value - r1 * 100]);
  }

  public static short encode2DigitsAsShort(int value) {
    return DIGITS_100[value];
  }

  public static void encodeUnsignedMax9x4(int value, ByteBuf buf) {
    var q1 = value / 100;
    if (q1 == 0) {
      var d2 = DIGITS_100[value];
      if (value < 10) {
        buf.writeByte(d2);
      } else {
        buf.writeShort(d2);
      }
    } else {
      var d1 = DIGITS_100[q1];
      var d2 = DIGITS_100[value - q1 * 100];
      if (q1 < 10) {
        buf.writeByte(d1);
      } else {
        buf.writeShort(d1);
      }
      buf.writeShort(d2);
    }
  }

  public static void encodeUnsignedMax9x6(int value, ByteBuf buf) {
    var q1 = value / 100;
    if (q1 == 0) {
      var d2 = DIGITS_100[value];
      if (value < 10) {
        buf.writeByte(d2);
      } else {
        buf.writeShort(d2);
      }
    } else {
      var q2 = q1 / 100;
      if (q2 == 0) {
        var d1 = DIGITS_100[q1];
        var d2 = DIGITS_100[value - q1 * 100];
        if (q1 < 10) {
          buf.writeByte(d1);
        } else {
          buf.writeShort(d1);
        }
        buf.writeShort(d2);
      } else {
        var d1 = DIGITS_100[q2];
        var d2 = DIGITS_100[q1 - q2 * 100];
        var d3 = DIGITS_100[value - q1 * 100];
        if (q2 < 10) {
          buf.writeByte(d1);
        } else {
          buf.writeShort(d1);
        }
        buf.writeShort(d2);
        buf.writeShort(d3);
      }
    }
  }

  public static void encodeUnsignedMax9x7(int value, ByteBuf buf) {
    var q1 = value / 100;
    if (q1 == 0) {
      var d2 = DIGITS_100[value];
      if (value < 10) {
        buf.writeByte(d2);
      } else {
        buf.writeShort(d2);
      }
    } else {
      var q2 = q1 / 100;
      if (q2 == 0) {
        var d1 = DIGITS_100[q1];
        var d2 = DIGITS_100[value - q1 * 100];
        if (q1 < 10) {
          buf.writeByte(d1);
        } else {
          buf.writeShort(d1);
        }
        buf.writeShort(d2);
      } else {
        var q3 = q2 / 100;
        if (q3 == 0) {
          var d1 = DIGITS_100[q2];
          var d2 = DIGITS_100[q1 - q2 * 100];
          var d3 = DIGITS_100[value - q1 * 100];
          if (q2 < 10) {
            buf.writeByte(d1);
          } else {
            buf.writeShort(d1);
          }
          buf.writeShort(d2);
          buf.writeShort(d3);
        } else {
          var d2 = DIGITS_100[q2 - q3 * 100];
          var d3 = DIGITS_100[q1 - q2 * 100];
          var d4 = DIGITS_100[value - q1 * 100];
          buf.writeByte(q3 + '0');
          buf.writeShort(d2);
          buf.writeShort(d3);
          buf.writeShort(d4);
        }
      }
    }
  }

  public static void encode(BigDecimal value, ByteBuf buf) {
    buf.writeCharSequence(value.toString(), StandardCharsets.UTF_8);
  }

}
