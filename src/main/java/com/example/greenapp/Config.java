package com.example.greenapp;

import io.netty.util.ResourceLeakDetector;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.LogManager;

public final class Config {
  public static final boolean WRITABLE_STACK_TRACE;

  static {
    var showtime = getShowtime();

    WRITABLE_STACK_TRACE = showtime != Showtime.ON;

    System.err.println("SHOWTIME             = " + showtime);
    System.err.println("WRITABLE_STACK_TRACE = " + WRITABLE_STACK_TRACE);

    configureLoggingLevel(showtime);
    configureLeakDetector(showtime);
  }

  public static void init() {
  }

  private static void configureLoggingLevel(Showtime showtime) {
    Level level = switch (showtime) {
      case ON -> Level.WARNING;
      case DEFAULT -> Level.INFO;
      case OFF -> Level.FINE;
    };
    System.err.println("Logging level is " + level);
    LogManager.getLogManager().getLogger("").setLevel(level);
  }

  private static void configureLeakDetector(Showtime showtime) {
    ResourceLeakDetector.Level level = switch (showtime) {
      case ON -> ResourceLeakDetector.Level.DISABLED;
      case DEFAULT -> ResourceLeakDetector.Level.SIMPLE;
      case OFF -> ResourceLeakDetector.Level.ADVANCED;
    };
    System.err.println("Leak detector level is " + level);
    ResourceLeakDetector.setLevel(level);
  }

  private static Showtime getShowtime() {
    if (LocalDateTime.of(2023, 5, 19, 23, 59, 59).isBefore(LocalDateTime.now())) {
      // Just to be sure :)
      return Showtime.ON;
    }
    String string = System.getProperty("greenApp.showtime");
    if (string == null || string.isBlank()) {
      return Showtime.DEFAULT;
    } else if (Boolean.parseBoolean(string)) {
      return Showtime.ON;
    } else {
      return Showtime.OFF;
    }
  }

  private enum Showtime {
    ON,
    OFF,
    DEFAULT,
  }

}
