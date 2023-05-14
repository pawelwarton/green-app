package com.example.greenapp.client;

import com.example.greenapp.NetworkConfig;
import com.example.greenapp.atmservice.TasksGenerator;
import com.example.greenapp.onlinegame.gen.PlayersGenerator;
import com.example.greenapp.transactions.TransactionsGenerator;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

import static com.example.greenapp.atmservice.Const.MAX_REGION;
import static com.example.greenapp.transactions.Const.MAX_TRANSACTION_COUNT;
import static java.nio.charset.StandardCharsets.UTF_8;

class RequestFactory {
  private final String address;

  public RequestFactory() {
    this(NetworkConfig.SOCKET_ADDRESS);
  }

  public RequestFactory(InetSocketAddress address) {
    this.address = address.getAddress().getHostAddress() + ':' + address.getPort();
  }

  public HttpRequest generateAtmsCalculateOrderRequest(int taskCount) {
    var requestBody = generateAtmsCalculateOrderRequestBody(taskCount);
    return generatePostRequest("/atms/calculateOrder", requestBody);
  }

  public HttpRequest generateOnlinegameCalculateOrderRequest() {
    var requestBody = generateOnlinegameCalculateOrderRequestBody();
    return generatePostRequest("/onlinegame/calculate", requestBody);
  }

  public HttpRequest generateTransactionsReportRequest() {
    var requestBody = generateTransactionsReportRequestBody();
    return generatePostRequest("/transactions/report", requestBody);
  }

  private HttpRequest generatePostRequest(String path, byte[] body) {
    return HttpRequest.newBuilder(URI.create("http://" + address + path))
      .version(HttpClient.Version.HTTP_1_1)
      .header("content-type", "application/json")
      .timeout(Duration.ofSeconds(2))
      .POST(HttpRequest.BodyPublishers.ofByteArray(body))
      .build();
  }

  private static byte[] generateAtmsCalculateOrderRequestBody(int taskCount) {
    var p1 = "{\"region\":".getBytes(UTF_8);
    var p2 = ",\"requestType\":\"".getBytes(UTF_8);
    var p3 = "\",\"atmId\":".getBytes(UTF_8);
    var regions = Math.min(taskCount / 10, MAX_REGION);
    var tasksPerRegion = taskCount / regions;
    var tasks = new TasksGenerator().generate(regions, tasksPerRegion, tasksPerRegion);
    var baos = new ByteArrayOutputStream();
    baos.write('[');
    var first = true;
    for (var task : tasks) {
      if (first) {
        first = false;
      } else {
        baos.write(',');
      }
      baos.writeBytes(p1);
      baos.writeBytes(Integer.toString(task.getRegion()).getBytes(UTF_8));
      baos.writeBytes(p2);
      baos.writeBytes(task.getRequestType().name().getBytes(UTF_8));
      baos.writeBytes(p3);
      baos.writeBytes(Integer.toString(task.getAtmId()).getBytes(UTF_8));
      baos.write('}');
    }
    baos.write(']');
    return baos.toByteArray();
  }

  private static byte[] generateOnlinegameCalculateOrderRequestBody() {
    var p1 = "{\"numberOfPlayers\":".getBytes(UTF_8);
    var p2 = ",\"points\":".getBytes(UTF_8);
    var players = new PlayersGenerator().generate();
    var baos = new ByteArrayOutputStream();
    baos.writeBytes(("{\"groupCount\":" + players.getGroupCount() + ",\"clans\":[").getBytes(UTF_8));
    var first = true;
    for (var clan : players.getClans()) {
      if (first) {
        first = false;
      } else {
        baos.write(',');
      }
      baos.writeBytes(p1);
      baos.writeBytes(Integer.toString(clan.getNumberOfPlayers()).getBytes(UTF_8));
      baos.writeBytes(p2);
      baos.writeBytes(Integer.toString(clan.getPoints()).getBytes(UTF_8));
      baos.write('}');
    }
    baos.write(']');
    baos.write('}');
    return baos.toByteArray();
  }

  private static byte[] generateTransactionsReportRequestBody() {
    var p1 = "{\"debitAccount\":\"".getBytes(UTF_8);
    var p2 = "\",\"creditAccount\":\"".getBytes(UTF_8);
    var p3 = "\",\"amount\":".getBytes(UTF_8);

    var transactions = new TransactionsGenerator().generate(5_000, MAX_TRANSACTION_COUNT);
    var baos = new ByteArrayOutputStream();
    baos.write('[');
    var first = true;
    for (var transaction : transactions) {
      if (first) {
        first = false;
      } else {
        baos.write(',');
      }
      baos.writeBytes(p1);
      baos.writeBytes(transaction.getDebitAccount().toString().getBytes(UTF_8));
      baos.writeBytes(p2);
      baos.writeBytes(transaction.getCreditAccount().toString().getBytes(UTF_8));
      baos.writeBytes(p3);
      baos.writeBytes(transaction.getAmount().toPlainString().getBytes(UTF_8));
      baos.write('}');
    }
    baos.write(']');
    return baos.toByteArray();
  }

}
