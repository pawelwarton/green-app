package com.example.greenapp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class AppInitializer extends ChannelInitializer<SocketChannel> {

  @Override
  public void initChannel(SocketChannel ch) {
    ch.pipeline()
      .addLast("encoder", buildHttpResponseEncoder())
      .addLast("decoder", buildHttpRequestDecoder())
      .addLast("aggregator", buildHttpObjectAggregator())
      .addLast("handler", buildAppHandler());
  }

  private static HttpResponseEncoder buildHttpResponseEncoder() {
    return new HttpResponseEncoder() {
      @Override
      public boolean acceptOutboundMessage(final Object msg) throws Exception {
        if (msg.getClass() == DefaultFullHttpResponse.class) {
          return true;
        }
        return super.acceptOutboundMessage(msg);
      }
    };
  }

  private static final int MAX_CONTENT_LENGTH = 25 * 1024 * 1024;

  private static HttpRequestDecoder buildHttpRequestDecoder() {
    var maxInitialLineLength = 1024;
    var maxHeaderSize = 8192;
    var maxChunkSize = maxInitialLineLength + maxHeaderSize + MAX_CONTENT_LENGTH;
    return new HttpRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, false);
  }

  private static HttpObjectAggregator buildHttpObjectAggregator() {
    return new HttpObjectAggregator(MAX_CONTENT_LENGTH, false);
  }

  private static AppHandler buildAppHandler() {
    return new AppHandler();
  }

}
