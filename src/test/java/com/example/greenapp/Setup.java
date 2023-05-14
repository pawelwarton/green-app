package com.example.greenapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;

public class Setup {
  private static final Pattern UBER_JAR = Pattern.compile("green-app-\\d+\\.\\d+\\.\\d+\\.jar");
  private static final Pattern PORT_LINE = Pattern.compile("Listening on .*:(\\d+)$");
  private static Process GREEN_APP;
  public static Integer GREEN_APP_PORT;
  public static String GREEN_APP_ADDRESS;

  public static void startGreenApp() {
    if (GREEN_APP != null) {
      return;
    }
    Path uberJarPath = findUberJarPath();

    GREEN_APP = startGreepApp(uberJarPath);
    Runtime.getRuntime().addShutdownHook(new Thread(Setup::killGreenApp));

    var greenAppStderr = GREEN_APP.getErrorStream();
    try {
      GREEN_APP_PORT = new BufferedReader(new InputStreamReader(greenAppStderr))
        .lines()
        .peek(System.err::println)
        .map(Setup::extractPort)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow();
      GREEN_APP_ADDRESS = "localhost:" + GREEN_APP_PORT;
    } catch (RuntimeException e1) {
      killGreenApp();
      try {
        greenAppStderr.close();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
      throw e1;
    }

    new Thread(() -> {
      try {
        greenAppStderr.transferTo(System.err);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }).start();
  }

  private static Path findUberJarPath() {
    Path uberJarPath;
    try (var paths = Files.list(Paths.get("target"))) {
      uberJarPath = paths
        .filter(Setup::isGreenAppUberJar)
        .findFirst()
        .orElseThrow();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return uberJarPath;
  }

  private static boolean isGreenAppUberJar(Path path) {
    return UBER_JAR.matcher(path.getFileName().toString()).matches();
  }

  private static Process startGreepApp(Path uberJarPath) {
    try {
      return Runtime.getRuntime().exec(new String[]{"java", "-DgreenApp.port=0", "-DgreenApp.showtime=false", "-jar", uberJarPath.toString()});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Integer extractPort(String stdErrLine) {
    var matcher = PORT_LINE.matcher(stdErrLine);
    if (!matcher.find()) {
      return null;
    } else {
      return Integer.valueOf(matcher.group(1));
    }
  }

  private static void killGreenApp() {
    if (GREEN_APP != null) {
      GREEN_APP.destroy();
      try {
        GREEN_APP.waitFor();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      GREEN_APP = null;
    }
    GREEN_APP_PORT = null;
    GREEN_APP_ADDRESS = null;
  }

}
