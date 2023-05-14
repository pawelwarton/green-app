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

public class AtmserviceIT {

  @BeforeAll // TODO: custom junit extension?
  static void startGreenApp() {
    Setup.startGreenApp();
  }

  @Test
  void testAtmserviceEndpoint1() throws Exception {
    testAtmServiceEndpoint("tasks/atmservice/example_1_request.json", "tasks/atmservice/example_1_response.json");
  }

  @Test
  void testAtmserviceEndpoint2() throws Exception {
    testAtmServiceEndpoint("tasks/atmservice/example_2_request.json", "tasks/atmservice/example_2_response.json");
  }

  private void testAtmServiceEndpoint(String requestBodyPath, String responseBodyPath) throws Exception {
    var exampleRequest = Files.readString(Paths.get(requestBodyPath));
    var exampleResponse = Files.readString(Paths.get(responseBodyPath));
    var response = postJson(URI.create("http://" + GREEN_APP_ADDRESS + "/atms/calculateOrder"), exampleRequest);
    assertEquals(200, response.statusCode());
    JSONAssert.assertEquals(exampleResponse, response.body(), JSONCompareMode.STRICT);
  }

}
