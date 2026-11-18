package edu.bu.queue.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.queue.data.DataStoreQueueService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.tinylog.Logger;

public class EnqueueHandler implements HttpHandler {
  final DataStoreQueueService dataStore;

  public EnqueueHandler(DataStoreQueueService dataStore) {
    this.dataStore = dataStore;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // parse out symbol of interest from request
    // Check if the request method is PUT
    String requestBody = "";
    String response;
    if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
      // Read the request body
      requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      //      System.out.println(exchange.getRequestBody());
    }

    dataStore.updateRequestQueue(requestBody);
    response = "Enqueued request";

    Logger.info("Queued request {}.", requestBody);

    exchange.sendResponseHeaders(200, response.length());

    //    OutputStream outputStream = exchange.getResponseBody();
    //    outputStream.write(response.getBytes());
    OutputStream outputStream = null;

    try {
      outputStream = exchange.getResponseBody();
      outputStream.write(response.getBytes()); // ??not sure if this is correct
    } finally {
      outputStream.close();
    }
  }
}
