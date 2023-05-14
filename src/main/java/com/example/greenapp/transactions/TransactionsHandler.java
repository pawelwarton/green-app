package com.example.greenapp.transactions;

import com.example.greenapp.JsonPostHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static com.example.greenapp.HttpUtil.buildOkResponse;

public class TransactionsHandler extends JsonPostHandler {
  private final BufSolver solver;

  public TransactionsHandler() {
    this(new BufSolver());
  }

  public TransactionsHandler(BufSolver solver) {
    this.solver = solver;
  }

  @Override
  protected void handleRequestContent(ChannelHandlerContext ctx, ByteBuf requestContent) {
    var responseContent = solver.solve(requestContent);
    ctx.write(buildOkResponse(responseContent), ctx.voidPromise());
  }

}
