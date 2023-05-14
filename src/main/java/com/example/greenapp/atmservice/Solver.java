package com.example.greenapp.atmservice;

import com.example.greenapp.annotations.Recycle;
import com.example.greenapp.annotations.Unsafe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Predicate;

import static com.example.greenapp.algorithms.Algorithms.PARALLEL;
import static com.example.greenapp.atmservice.Const.MAX_ATM_ID;
import static com.example.greenapp.atmservice.Const.MAX_REGION;
import static com.example.greenapp.atmservice.Const.MIN_ATM_ID;
import static com.example.greenapp.atmservice.Const.MIN_REGION;

@SuppressWarnings("ComparatorCombinators")
public class Solver {
  public static final int GROUPING_THRESHOLD = 400;
  public static final int PARALLEL_THRESHOLD = 1_000;

  /**
   * @param tasks the list might be modified during method call
   * @return sorted and deduplicates list of tasks
   */
  @Unsafe
  public Iterable<? extends ATM> solve(Tasks tasks) {
    if (tasks.size() < 2) {
      return tasks;
    }
    if (tasks.size() < GROUPING_THRESHOLD) {
      return solveBySorting(tasks);
    } else {
      return solverByGrouping(tasks);
    }
  }

  private Iterable<? extends ATM> solveBySorting(Tasks tasks) {
    tasks.sort((a, b) -> {
      if (a.getRegion() != b.getRegion()) {
        return a.getRegion() - b.getRegion();
      }
      return a.getRequestType().order - b.getRequestType().order;
    });
    tasks.removeIf(new RemoveDuplicateTasksInEachRegionPredicate());
    return tasks;
  }

  private Iterable<? extends ATM> solverByGrouping(Tasks tasks) {
    var tasksByRegion = new Tasks[MAX_REGION + 1];
    for (var task : tasks) {
      var tasksForRegion = tasksByRegion[task.getRegion()];
      if (tasksForRegion == null) {
        tasksForRegion = tasksByRegion[task.getRegion()] = new Tasks(8);
      }
      tasksForRegion.add(task);
    }
    var taskCount = tasks.size();
    tasks.clear();
    if (!PARALLEL || taskCount < PARALLEL_THRESHOLD) {
      solveGrouped(tasksByRegion, tasks);
    } else {
      solveGroupedInParallel(tasksByRegion, tasks);
    }
    return tasks;
  }

  private void solveGrouped(Tasks[] tasksByRegion, Tasks accumulator) {
    var removeDuplicateTaskPredicate = new RemoveDuplicateTasksPredicate();
    var first = true;
    for (var tasksForRegion : tasksByRegion) {
      if (tasksForRegion != null) {
        if (tasksForRegion.size() > 1) {
          if (first) {
            first = false;
          } else {
            removeDuplicateTaskPredicate.clear();
          }
          tasksForRegion.removeIf(removeDuplicateTaskPredicate);
          sortTasksByRequestType(tasksForRegion);
        }
        accumulator.addAll(tasksForRegion);
      }
    }
  }

  private void solveGroupedInParallel(Tasks[] tasksByRegion, Tasks accumulator) {
    var pool = ForkJoinPool.commonPool();
    var poolTasks = new ArrayList<ForkJoinTask<Tasks>>();
    for (var tasksForRegion : tasksByRegion) {
      if (tasksForRegion != null) {
        poolTasks.add(pool.submit(() -> {
          if (tasksForRegion.size() > 1) {
            tasksForRegion.removeIf(new RemoveDuplicateTasksPredicate());
            sortTasksByRequestType(tasksForRegion);
          }
          return tasksForRegion;
        }));
      }
    }
    for (var poolTask : poolTasks) {
      accumulator.addAll(poolTask.join());
    }
  }

  private static void sortTasksByRequestType(Tasks tasks) {
    tasks.sort((a, b) -> a.getRequestType().order - b.getRequestType().order);
  }

  private static class RemoveDuplicateTasksPredicate implements Predicate<Task> {
    static {
      if (MIN_ATM_ID < 0 || MAX_ATM_ID < 0 || 15_000 < MAX_ATM_ID) {
        throw new LinkageError();
      }
    }
    protected final long[] words = new long[MAX_ATM_ID / Long.SIZE + 1];

    @Recycle
    public void clear() {
      Arrays.fill(this.words, 0L);
    }

    @Override
    public boolean test(Task task) {
      int wordIdx = task.getAtmId() / Long.SIZE;
      long atmIdMask = 1L << task.getAtmId();
      long word = this.words[wordIdx];
      if ((word & atmIdMask) != 0L) {
        return true;
      }
      this.words[wordIdx] = word | atmIdMask;
      return false;
    }
  }

  /**
   * Assumes that tasks will be sorted by region, meaning that tasks from the
   * same region will be adjacent.
   */
  private static class RemoveDuplicateTasksInEachRegionPredicate extends RemoveDuplicateTasksPredicate {
    private static final int NEED_TO_ZERO_REGION = MIN_REGION - 1;
    private static final int NO_NEED_TO_ZERO_REGION = MIN_REGION - 2;
    private int region = NO_NEED_TO_ZERO_REGION;

    @Recycle
    @Override
    public void clear() {
      this.region = NEED_TO_ZERO_REGION;
    }

    @Override
    public boolean test(Task task) {
      if (this.region != task.getRegion()) {
        if (this.region != NO_NEED_TO_ZERO_REGION) {
          Arrays.fill(this.words, 0L);
        }
        this.region = task.getRegion();
      }
      return super.test(task);
    }
  }

}
