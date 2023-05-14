package com.example.greenapp.atmservice;

import com.example.greenapp.TenRpsPool;
import com.example.greenapp.annotations.ThreadSafe;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.atmservice.endec.Decoder.decode;
import static com.example.greenapp.atmservice.endec.Encoder.encode;

@ThreadSafe("The pool makes this thread-safe")
class BufSolver {

  private final TenRpsPool<PoolEntry> pool = new TenRpsPool<>() {
    @Override
    protected PoolEntry build() {
      return new PoolEntry(new Tasks(), new Solver());
    }

    @Override
    public void recycle(PoolEntry entry) {
      entry.tasks.clear();
      super.recycle(entry);
    }
  };

  /**
   * @param requestContent mutably borrowed
   */
  public ByteBuf solve(ByteBuf requestContent) {
    var entry = pool.get();
    try {
      decode(requestContent, entry.tasks);
      var atms = entry.solver.solve(entry.tasks);
      requestContent.clear();
      var responseContent = requestContent.retain();
      try {
        encode(atms, requestContent);
      } catch (Throwable t) {
        responseContent.release();
        throw t;
      }
      return responseContent;
    } finally {
      pool.recycle(entry);
    }
  }

  private record PoolEntry(Tasks tasks, Solver solver) {
  }

}
