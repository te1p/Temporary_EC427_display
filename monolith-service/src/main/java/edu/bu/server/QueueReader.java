package edu.bu.server;

import static java.lang.Math.pow;

import edu.bu.data.DataStore;
import edu.bu.finhub.FinhubParser;
import edu.bu.finhub.FinhubResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
// import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.tinylog.Logger;

public class QueueReader {
  final String queueURL = "http://localhost:8010/dequeue";
  final FinhubParser finhubParser;
  final DataStore dataStore;
  int sleepTime = 25;
  int counter = 1;

  public QueueReader(FinhubParser finhubParser, DataStore dataStore) {
    this.finhubParser = finhubParser;
    this.dataStore = dataStore;
  }

  public HttpResponse<String> queryQueueService(String URL) {

    HttpClient client = HttpClient.newHttpClient();
    try {
      HttpRequest request = HttpRequest.newBuilder().uri(new URI(URL)).GET().build();
      return client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException | URISyntaxException e) {
      Logger.error(e);
      throw new RuntimeException(e);
    } finally {
      client.close();
      System.gc();
    }
  }

  public int handle() {

    HttpResponse<String> response = queryQueueService(queueURL);

    if (response.statusCode() == 200) {
      sleepTime = 25;
      counter = 1;
      List<FinhubResponse> finhubResponses = finhubParser.parse(response.body());
      dataStore.update(finhubResponses);
      Logger.info("Received FinHub response {}", response.body());
      return sleepTime;
    } else {
      counter++;
      sleepTime = sleepTime < 120000 ? (int) pow(sleepTime, counter) : sleepTime;
      Logger.info("FinHub response was empty");
      return sleepTime;
    }
  }
}
