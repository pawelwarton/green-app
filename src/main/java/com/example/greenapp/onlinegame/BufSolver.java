package com.example.greenapp.onlinegame;

import com.example.greenapp.TenRpsPool;
import com.example.greenapp.annotations.ThreadSafe;
import io.netty.buffer.ByteBuf;

import static com.example.greenapp.onlinegame.endec.Decoder.decode;
import static com.example.greenapp.onlinegame.endec.Encoder.encode;

@ThreadSafe("The pool makes this thread-safe")
class BufSolver {

  private final TenRpsPool<PoolEntry> pool = new TenRpsPool<>() {
    @Override
    protected PoolEntry build() {
      var players = new Players();
      players.clans = new Clans();
      return new PoolEntry(players, new Solver());
    }

    @Override
    public void recycle(PoolEntry entry) {
      entry.players.groupCount = 0;
      entry.players.clans.clear();
      super.recycle(entry);
    }
  };

  /**
   * @param requestContent mutably borrowed
   */
  public ByteBuf solve(ByteBuf requestContent) {
    var entry = pool.get();
    try {
      decode(requestContent, entry.players);
      var groups = entry.solver.solve(entry.players);
      requestContent.clear();
      var responseContent = requestContent.retain();
      try {
        encode(groups, requestContent);
      } catch (Throwable t) {
        responseContent.release();
        throw t;
      }
      return responseContent;
    } finally {
      pool.recycle(entry);
    }
  }

  private record PoolEntry(Players players, Solver solver) {
  }

}
