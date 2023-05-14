package com.example.greenapp.atmservice;

import com.example.greenapp.JsonPostHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static com.example.greenapp.HttpUtil.buildOkResponse;

public class AtmServiceHandler extends JsonPostHandler {
  private final BufSolver solver;

  public AtmServiceHandler() {
    this(new BufSolver());
  }

  public AtmServiceHandler(BufSolver solver) {
    this.solver = solver;
  }

  @Override
  protected void handleRequestContent(ChannelHandlerContext ctx, ByteBuf requestContent) {
    var responseContent = solver.solve(requestContent);
    ctx.write(buildOkResponse(responseContent), ctx.voidPromise());
  }
}
