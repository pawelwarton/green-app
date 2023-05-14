package com.example.greenapp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.example.greenapp.HttpTestUtil.postJson;
import static com.example.greenapp.Setup.GREEN_APP_ADDRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionsIT {

  @BeforeAll // TODO: custom junit extension?
  static void startGreenApp() {
    Setup.startGreenApp();
  }

  @Test
  void testTransactionsEndpoint() throws Exception {
    var exampleRequest = Files.readString(Paths.get("tasks/transactions/example_request.json"));
    var exampleResponse = Files.readString(Paths.get("tasks/transactions/example_response.json"));
    var response = postJson(URI.create("http://" + GREEN_APP_ADDRESS + "/transactions/report"), exampleRequest);
    assertEquals(200, response.statusCode());
    JSONAssert.assertEquals(exampleResponse, response.body(), JSONCompareMode.STRICT);
  }

}
