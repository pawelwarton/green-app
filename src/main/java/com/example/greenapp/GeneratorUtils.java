package com.example.greenapp;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GeneratorUtils {

  public static byte[] genWhitespace(Random rng, int len) {
    var result = new byte[len];
    for (int i = 0; i < len; i++) {
      result[i] = switch (rng.nextInt(4)) {
        case 0 -> ' ';
        case 1 -> '\n';
        case 2 -> '\r';
        case 3 -> '\t';
        default -> throw new Error();
      };
    }
    return result;
  }

  public static byte[] genAsciiDigits(Random rng, int len) {
    var result = new byte[len];
    for (var i = 0; i < len; i++) {
      result[i] = (byte) ('0' + rng.nextInt(0, 10));
    }
    return result;
  }

  public static String genAsciiDigitsString(Random rng, int len) {
    return new String(genAsciiDigits(rng, len), StandardCharsets.UTF_8);
  }

  public static String genAsciiDigitsString(int len) {
    return genAsciiDigitsString(ThreadLocalRandom.current(), len);
  }

  public static BigDecimal genPln(Random rng, long originGrosz, long boundGrosz) {
    var amount = rng.nextLong(originGrosz, boundGrosz);
    return new BigDecimal(amount).movePointLeft(2);
  }

  public static BigDecimal genPln(long originGrosz, long boundGrosz) {
    return genPln(ThreadLocalRandom.current(), originGrosz, boundGrosz);
  }

  public static int genInt(int origin, int bound) {
    return ThreadLocalRandom.current().nextInt(origin, bound);
  }

  public static <T extends Enum<T>> T choose(Random rng, Class<T> clazz) {
    var constants = clazz.getEnumConstants();
    return constants[rng.nextInt(constants.length)];
  }

  public static <T extends Enum<T>> T choose(Class<T> clazz) {
    return choose(ThreadLocalRandom.current(), clazz);
  }

  public static <T> T choose(Random rng, List<T> ts) {
    return ts.get(rng.nextInt(0, ts.size()));
  }

}
