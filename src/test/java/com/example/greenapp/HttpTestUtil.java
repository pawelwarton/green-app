package com.example.greenapp;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

class HttpTestUtil {
  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

  public static HttpResponse<String> postJson(URI uri, String body) throws Exception {
    var request = HttpRequest.newBuilder(uri)
      .version(HttpClient.Version.HTTP_1_1)
      .header("content-type", "application/json")
      .timeout(Duration.ofSeconds(2))
      .POST(HttpRequest.BodyPublishers.ofString(body))
      .build();
    return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
  }

}

