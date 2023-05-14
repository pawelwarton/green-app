package com.example.greenapp.onlinegame;

import com.example.greenapp.JsonPostHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static com.example.greenapp.HttpUtil.buildOkResponse;

public class OnlineGameHandler extends JsonPostHandler {
  private final BufSolver solver;

  public OnlineGameHandler() {
    this(new BufSolver());
  }

  public OnlineGameHandler(BufSolver solver) {
    this.solver = solver;
  }

  @Override
  protected void handleRequestContent(ChannelHandlerContext ctx, ByteBuf requestContent) {
    var responseContent = solver.solve(requestContent);
    ctx.write(buildOkResponse(responseContent), ctx.voidPromise());
  }
}
