package com.example.greenapp.algorithms;

import java.util.Arrays;
import java.util.Comparator;

public class Algorithms {
  public static final boolean PARALLEL;

  static {
    PARALLEL = Runtime.getRuntime().availableProcessors() > 1;
    System.err.println("Parallel processing is " + (PARALLEL ? "enabled" : "disabled"));
  }

  public static <T extends Comparable<? super T>> void sort(T[] a, int fromIndex, int toIndex) {
    if (PARALLEL) {
      Arrays.parallelSort(a, fromIndex, toIndex);
    } else {
      Arrays.sort(a, fromIndex, toIndex);
    }
  }

  public static <T> void sort(T[] a, int fromIndex, int toIndex, Comparator<? super T> cmp) {
    if (PARALLEL) {
      Arrays.parallelSort(a, fromIndex, toIndex, cmp);
    } else {
      Arrays.sort(a, fromIndex, toIndex, cmp);
    }
  }

}
