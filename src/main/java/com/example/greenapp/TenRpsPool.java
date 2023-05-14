package com.example.greenapp;

import com.example.greenapp.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicReferenceArray;

import static com.example.greenapp.Const.RPS;

@ThreadSafe("Uses atomic operations")
public abstract class TenRpsPool<T> {
  static {
    if (20 < RPS) {
      throw new LinkageError();
    }
  }

  private static final int SIZE = RPS;
  private final AtomicReferenceArray<T> refs = new AtomicReferenceArray<>(SIZE);

  protected abstract T build();

  public T get() {
    for (int i = 0; i < SIZE; i++) {
      var ref = refs.getAndSet(i, null);
      if (ref != null) {
        return ref;
      }
    }
    return build();
  }

  public void recycle(T entry) {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < SIZE; j++) {
        if (refs.compareAndSet(j, null, entry)) {
          return;
        }
      }
    }
  }
}
