package com.example.greenapp.transactions;

import com.example.greenapp.transactions.slow.Solver;
import com.example.greenapp.transactions.slow.Transactions;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.transactions.slow.Decoder.decode;
import static com.example.greenapp.transactions.slow.Encoder.encode;

class SlowBufSolver {
  private final Transactions transactions = new Transactions();
  private final Solver solver = new Solver();

  public ByteBuf solve(ByteBuf requestContent) {
    var transactions = this.transactions;
    try {
      decode(requestContent, transactions);
      var accounts = solver.solve(transactions);
      requestContent.clear();
      var responseContent = requestContent.retain();
      try {
        encode(accounts, requestContent);
      } catch (Throwable t) {
        responseContent.release();
        throw t;
      }
      return responseContent;
    } finally {
      transactions.clear();
    }
  }
}
