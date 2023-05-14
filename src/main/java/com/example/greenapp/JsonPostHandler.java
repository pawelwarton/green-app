package com.example.greenapp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import static com.example.greenapp.HttpUtil.buildMethodNotAllowedResponse;
import static com.example.greenapp.HttpUtil.buildNotAcceptableResponse;
import static com.example.greenapp.HttpUtil.isMimeTypeApplicationJson;
import static com.example.greenapp.HttpUtil.isPostMethod;

public abstract class JsonPostHandler {

  protected abstract void handleRequestContent(ChannelHandlerContext ctx, ByteBuf requestContent);

  public void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
    if (!isPostMethod(request)) {
      ctx.write(buildMethodNotAllowedResponse(), ctx.voidPromise());
    } else if (!isMimeTypeApplicationJson(request)) {
      ctx.write(buildNotAcceptableResponse(), ctx.voidPromise());
    } else {
      var requestContent = request.content();
      if (requestContent instanceof CompositeByteBuf compositeByteBuf) {
        // This is OK, because CompositeByteBuf on release,
        // just releases its children.
        requestContent = compositeByteBuf.consolidate().component(0);
      }
      handleRequestContent(ctx, requestContent);
    }
  }

}
