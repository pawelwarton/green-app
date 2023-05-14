package com.example.greenapp;

import com.example.greenapp.atmservice.AtmServiceHandler;
import com.example.greenapp.onlinegame.OnlineGameHandler;
import com.example.greenapp.transactions.TransactionsHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.greenapp.HttpUtil.buildNotFoundResponse;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

class AppHandler extends ChannelInboundHandlerAdapter {
  private static final Logger LOGGER = Logger.getLogger(AppHandler.class.getName());

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
      process(ctx, (FullHttpRequest) msg);
    } catch (IndexOutOfBoundsException e) {
      LOGGER.log(Level.INFO, "Index out of bounds", e);
      ctx.write(makeBadRequestResponse(e), ctx.voidPromise());
    } catch (BadRequestException e) {
      LOGGER.log(Level.INFO, "Bad request", e);
      ctx.write(makeBadRequestResponse(e), ctx.voidPromise());
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  private final AtmServiceHandler atmServiceHandler = new AtmServiceHandler();
  private final OnlineGameHandler onlineGameHandler = new OnlineGameHandler();
  private final TransactionsHandler transactionsHandler = new TransactionsHandler();

  private void process(ChannelHandlerContext ctx, FullHttpRequest request) {
    switch (request.uri()) {
      case "/atms/calculateOrder" -> atmServiceHandler.handleRequest(ctx, request);
      case "/onlinegame/calculate" -> onlineGameHandler.handleRequest(ctx, request);
      case "/transactions/report" -> transactionsHandler.handleRequest(ctx, request);
      default -> ctx.write(buildNotFoundResponse()).addListener(ChannelFutureListener.CLOSE);
    }
  }

  private FullHttpResponse makeBadRequestResponse(IndexOutOfBoundsException e) {
    return makeBadRequestResponse("Invalid input");
  }

  private FullHttpResponse makeBadRequestResponse(BadRequestException e) {
    return makeBadRequestResponse(e.getMessage());
  }

  private FullHttpResponse makeBadRequestResponse(String message) {
    var content = message == null
      ? Unpooled.EMPTY_BUFFER
      : Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8));
    var response = new DefaultFullHttpResponse(
      HTTP_1_1,
      BAD_REQUEST,
      content,
      false
    );
    response.headers()
      .set(CONTENT_TYPE, TEXT_PLAIN)
      .set(CONTENT_LENGTH, Integer.toString(content.readableBytes()));
    return response;
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public boolean isSharable() {
    return true;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    LOGGER.log(Level.SEVERE, "Uncaught exception", cause);
    ctx.close();
  }

}
