package com.example.greenapp;

import java.net.InetSocketAddress;

import static com.example.greenapp.StringUtil.isBlank;

public class NetworkConfig {
  public static final InetSocketAddress SOCKET_ADDRESS;
  static {
    initIpStack();
    SOCKET_ADDRESS = getSocketAddress();
  }

  public static void init() {
  }

  private static void initIpStack() {
    String key = "java.net.preferIPv4Stack";
    if (System.getProperty(key) == null) {
      System.setProperty(key, "true");
    }
  }

  private static InetSocketAddress getSocketAddress() {
    String hostname = getHostname();
    int port = getPort();
    if (isBlank(hostname)) {
      // Spec says localhost, but it also says that the currency is
      // sent as a float, so we are binding to all interfaces.
      return new InetSocketAddress(port);
    } else {
      return new InetSocketAddress(hostname, port);
    }
  }

  private static String getHostname() {
    return System.getProperty("greenApp.hostname");
  }

  private static int getPort() {
    String portParam = System.getProperty("greenApp.port");
    return isBlank(portParam) ? 8080 : Integer.parseUnsignedInt(portParam);
  }

}
