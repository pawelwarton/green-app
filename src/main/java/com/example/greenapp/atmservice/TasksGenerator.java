package com.example.greenapp.atmservice;

import com.example.greenapp.GeneratorUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.greenapp.atmservice.Const.MAX_ATM_ID;
import static com.example.greenapp.atmservice.Const.MAX_REGION;
import static com.example.greenapp.atmservice.Const.MIN_ATM_ID;
import static com.example.greenapp.atmservice.Const.MIN_REGION;

public class TasksGenerator {

  private final Random rng;

  public TasksGenerator() {
    this(new Random(551));
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = """
    Yeah it may. That's kind of the point. We want to allow external code to
    control the rng, so that the results are predictable and not repeating
    when creating multiple generators.
    """)
  public TasksGenerator(Random rng) {
    this.rng = rng;
  }

  private static ArrayList<Integer> buildAllIds(int minId, int maxId) {
    return IntStream
      .rangeClosed(minId, maxId)
      .boxed()
      .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<Task> generate(int regionCount, int minTasksPerRegion, int maxTasksPerRegion) {
    var tasks = new ArrayList<Task>();

    var regions = buildAllIds(MIN_REGION, MAX_REGION);
    Collections.shuffle(regions, this.rng);

    for (int i = 0; i < regionCount; i++) {
      var region = regions.remove(regions.size() - 1);
      var tasksCount = this.rng.nextInt(minTasksPerRegion, maxTasksPerRegion + 1);

      for (int j = 0; j < tasksCount; j++) {
        var atmId = rng.nextInt(MIN_ATM_ID, MAX_ATM_ID);
        var requestType = this.genRequestType();

        tasks.add(new Task(region, atmId, requestType));
      }
    }

    Collections.shuffle(tasks, this.rng);
    return tasks;
  }

  private RequestType genRequestType() {
    return GeneratorUtils.choose(rng, RequestType.class);
  }
}
