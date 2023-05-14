package com.example.greenapp.transactions;

import com.example.greenapp.OptimizationAssumptionException;
import com.example.greenapp.TenRpsPool;
import com.example.greenapp.annotations.ThreadSafe;
import io.netty.buffer.ByteBuf;

@ThreadSafe("The pools make this thread-safe")
class BufSolver {
  private final TenRpsPool<FastBufSolver> fastSolverPool = new TenRpsPool<>() {
    @Override
    protected FastBufSolver build() {
      return new FastBufSolver();
    }
  };
  private final TenRpsPool<SlowBufSolver> slowSolverPool = new TenRpsPool<>() {
    @Override
    protected SlowBufSolver build() {
      return new SlowBufSolver();
    }
  };

  public ByteBuf solve(ByteBuf requestContent) {
    var readerIndex = requestContent.readerIndex();
    try {
      return solveFast(requestContent);
    } catch (OptimizationAssumptionException e) {
      return solveSlow(requestContent.readerIndex(readerIndex));
    }
  }

  private ByteBuf solveFast(ByteBuf requestContent) {
    var solver = fastSolverPool.get();
    try {
      return solver.solve(requestContent);
    } finally {
      fastSolverPool.recycle(solver);
    }
  }

  private ByteBuf solveSlow(ByteBuf requestContent) {
    var solver = slowSolverPool.get();
    try {
      return solver.solve(requestContent);
    } finally {
      slowSolverPool.recycle(solver);
    }
  }

}
