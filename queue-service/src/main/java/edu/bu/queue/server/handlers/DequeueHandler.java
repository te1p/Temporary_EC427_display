package edu.bu.queue.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.queue.data.DataStoreQueueService;
import java.io.IOException;
import java.io.OutputStream;
import org.tinylog.Logger;

public class DequeueHandler implements HttpHandler {
  final DataStoreQueueService dataStore;

  public DequeueHandler(DataStoreQueueService dataStore) {
    this.dataStore = dataStore;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    String requestBody = dataStore.PollRequestQueue();
    requestBody = requestBody == null ? "" : requestBody;
    String response = "";
    //    LocalDateTime dateTime = LocalDateTime.now();

    if (requestBody.isEmpty()) {
      response = "Request Body was empty";

      exchange.sendResponseHeaders(204, response.length());
      Logger.info("Dequeued request was empty.");
    } else {
      response = requestBody;
      //          "HTTP/1.1 200 OK\n"
      //              + "Date: "
      //              + dateTime
      //              + "\n"
      //              + "Content-length: "
      //              + requestLength
      //              + "\n\n"
      //              + requestBody;

      exchange.sendResponseHeaders(200, response.length());
      Logger.info("Dequeued request {}", response);
    }

    OutputStream outputStream = null;

    try {
      outputStream = exchange.getResponseBody();
      outputStream.write(response.getBytes());
    } finally {
      outputStream.close();
    }
  }
}
