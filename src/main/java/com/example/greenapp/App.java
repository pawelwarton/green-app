package com.example.greenapp;

import com.example.greenapp.client.AppClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.incubator.channel.uring.IOUring;
import io.netty.incubator.channel.uring.IOUringEventLoopGroup;
import io.netty.incubator.channel.uring.IOUringServerSocketChannel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
  static {
    Config.init();
    NetworkConfig.init();
  }
  private static final Logger LOGGER = Logger.getLogger(App.class.getName());

  public static void main(String[] args) throws Exception {
    run();
  }

  public static void run() throws Exception {
    if (IOUring.isAvailable()) {
      try {
        doRun(new IOUringEventLoopGroup(), IOUringServerSocketChannel.class);
        return;
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to start with io_uring event loop despite it being available", e);
      }
    }
    if (Epoll.isAvailable()) {
      try {
        doRun(new EpollEventLoopGroup(), EpollServerSocketChannel.class);
        return;
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to start with epoll event loop despite it being available", e);
      }
    }
    doRun(new NioEventLoopGroup(), NioServerSocketChannel.class);
  }

  private static void doRun(EventLoopGroup loopGroup, Class<? extends ServerChannel> serverChannelClass) throws InterruptedException {
    LOGGER.log(Level.INFO, "Using {0} channel", serverChannelClass.getName());
    try {
      var bootstrap = new ServerBootstrap();

      var channel = bootstrap
        .option(ChannelOption.SO_BACKLOG, 16)
        .group(loopGroup)
        .channel(serverChannelClass)
        .childHandler(new AppInitializer())
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .bind(NetworkConfig.SOCKET_ADDRESS)
        .sync()
        .channel();

      LOGGER.log(Level.INFO, "Listening on {0}", channel.localAddress());

      AppClient.runEmbedded();

      channel.closeFuture().sync();
    } finally {
      loopGroup.shutdownGracefully().sync();
    }
  }

}
