package com.example.greenapp.algorithms;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T> {
  private final T[] array;
  private final int untilIdx;
  private int idx;

  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = """
    Indeed the invoker of this constructor may manipulate output of this
    iterator. That's why they created the iterator in the first place.
    They can manipulate the results regardless of whether the array gets cloned.

    Also, this is application code only used by the application it is defined in.
    No "untrusted" code gets involved.

    This rule is outdated. Applets were removed in Java 17 and
    the security manager is deprecated for REMOVAL.
    """)
  public ArrayIterator(T[] array, int fromIdx, int untilIdx) {
    this.array = array;
    this.idx = fromIdx;
    this.untilIdx = untilIdx;
  }

  @Override
  public boolean hasNext() {
    return idx < untilIdx;
  }

  @Override
  public T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return array[idx++];
  }

}
