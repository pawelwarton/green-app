package com.example.greenapp.atmservice;

import com.example.greenapp.algorithms.Algorithms;
import com.example.greenapp.algorithms.ArrayIterator;
import com.example.greenapp.annotations.Recycle;
import com.example.greenapp.annotations.Unsafe;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Tasks implements Iterable<Task> {
  private static final Task[] EMPTY = {
  };
  private Task[] tasks;
  private int size;

  public Tasks() {
    this.tasks = EMPTY;
  }

  public Tasks(int initialCapacity) {
    this.tasks = new Task[initialCapacity];
  }

  public void add(Task task) {
    var tasks = this.tasks;
    if (size == tasks.length) {
      tasks = this.tasks = Arrays.copyOf(tasks, Math.max(8, tasks.length * 2));
    }
    tasks[size++] = task;
  }

  /**
   * This method might create a new task or reuse an old task. Those old tasks
   * should not be stored elsewhere.
   */
  @Unsafe
  public void add(int region, RequestType requestType, int atmId) {
    var tasks = this.tasks;
    if (size == tasks.length) {
      tasks = this.tasks = Arrays.copyOf(tasks, Math.max(8, tasks.length * 2));
    }
    var task = tasks[size++];
    if (task == null) {
      task = tasks[size - 1] = new Task();
    }
    task.setRegion(region);
    task.setRequestType(requestType);
    task.setAtmId(atmId);
  }

  public void addAll(Tasks other) {
    var tasks = this.tasks;
    if (tasks.length < size + other.size) {
      tasks = this.tasks = Arrays.copyOf(tasks, Math.max(tasks.length * 2, size + other.size));
    }
    System.arraycopy(other.tasks, 0, tasks, size, other.size);
    this.size += other.size;
  }

  public void addAll(List<Task> other) {
    var otherArr = other.toArray(Task[]::new);
    var tasks = this.tasks;
    if (tasks.length < size + otherArr.length) {
      tasks = this.tasks = Arrays.copyOf(tasks, Math.max(tasks.length * 2, size + otherArr.length));
    }
    System.arraycopy(otherArr, 0, tasks, size, otherArr.length);
    this.size += otherArr.length;
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  @Recycle
  public void clear() {
    size = 0;
  }

  public void sort(Comparator<Task> c) {
    Algorithms.sort(this.tasks, 0, this.size, c);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  public void removeIf(Predicate<Task> filter) {
    Objects.requireNonNull(filter);
    var tasks = this.tasks;
    var size = this.size;
    var leader = 0;
    for (; leader < size && !filter.test(tasks[leader]); leader++) ;

    if (leader != size) {
      var follower = leader;
      for (; leader < size; leader++) {
        var task = tasks[leader];
        if (!filter.test(task)) {
          tasks[leader] = tasks[follower];
          tasks[follower++] = task;
        }
      }
      this.size = follower;
    }
  }

  @Override
  public Iterator<Task> iterator() {
    return new ArrayIterator<>(tasks, 0, size);
  }

}
