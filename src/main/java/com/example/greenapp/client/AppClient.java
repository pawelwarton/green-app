package com.example.greenapp.client;

import com.example.greenapp.atmservice.Solver;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppClient {
  private static final Logger LOGGER = Logger.getLogger(AppClient.class.getName());

  public static void runEmbedded() {
    new Thread(() -> {
      try {
        new AppClient().run();
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "App client failed", e);
      }
      cleanUp();
    }).start();
  }

  @SuppressFBWarnings(value = "DM_GC", justification = "This will be run only once during the lifetime of this application")
  private static void cleanUp() {
    System.runFinalization();
    System.gc();
    System.runFinalization();
    System.gc();
  }

  private void run() throws Exception {
    var factory = new RequestFactory();
    var deadline = Instant.now().plus(150, ChronoUnit.SECONDS).toEpochMilli();
    var requests = new HttpRequest[]{
      factory.generateTransactionsReportRequest(),
      factory.generateOnlinegameCalculateOrderRequest(),
      factory.generateAtmsCalculateOrderRequest(100_000),
      factory.generateAtmsCalculateOrderRequest((Solver.PARALLEL_THRESHOLD - Solver.GROUPING_THRESHOLD) / 2 + Solver.GROUPING_THRESHOLD),
      factory.generateAtmsCalculateOrderRequest(Solver.GROUPING_THRESHOLD / 2),
    };

    var httpClient = HttpClient.newHttpClient();
    while (System.currentTimeMillis() < deadline) {
      for (var request : requests) {
        for (int i = 0; i < 400 && System.currentTimeMillis() < deadline; i++) {
          HttpResponse<Void> response = httpClient.send(request, BodyHandlers.discarding());
          if (response.statusCode() == 200) {
            this.printProgress();
          } else {
            LOGGER.log(Level.WARNING, "{0} to {1} failed with status code {2}", new Object[]{request.method(), request.uri(), response.statusCode()});
            return;
          }
        }
      }
    }
  }

  private int progress;

  private void printProgress() {
    System.err.print('.');
    if (++progress % 100 == 0) {
      System.err.println();
    }
  }

}
