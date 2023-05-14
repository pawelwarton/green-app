package com.example.greenapp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_ACCEPTABLE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.getMimeType;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpUtil {

  public static boolean isPostMethod(HttpRequest message) {
    return HttpMethod.POST.equals(message.method());
  }

  public static boolean isMimeTypeApplicationJson(HttpMessage message) {
    if (message.headers().contains(CONTENT_TYPE, APPLICATION_JSON, false)) {
      return true;
    }
    var contentType = message.headers().get(CONTENT_TYPE);
    return contentType != null
      && APPLICATION_JSON.contentEqualsIgnoreCase(getMimeType(contentType));
  }

  public static FullHttpResponse buildOkResponse(ByteBuf buf) {
    var response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf, false);
    response.headers()
      .set(CONTENT_TYPE, APPLICATION_JSON)
      .set(CONTENT_LENGTH, Integer.toString(buf.readableBytes()));
    return response;
  }

  public static FullHttpResponse buildNotFoundResponse() {
    return new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, Unpooled.EMPTY_BUFFER, false);
  }

  public static FullHttpResponse buildMethodNotAllowedResponse() {
    return new DefaultFullHttpResponse(HTTP_1_1, METHOD_NOT_ALLOWED, Unpooled.EMPTY_BUFFER, false);
  }

  public static FullHttpResponse buildNotAcceptableResponse() {
    return new DefaultFullHttpResponse(HTTP_1_1, NOT_ACCEPTABLE, Unpooled.EMPTY_BUFFER, false);
  }

}
