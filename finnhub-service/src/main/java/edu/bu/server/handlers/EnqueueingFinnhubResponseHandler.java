package edu.bu.server.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.tinylog.Logger;

public class EnqueueingFinnhubResponseHandler {

  final String queueURL = "http://localhost:8010/enqueue";

  public EnqueueingFinnhubResponseHandler() {}

  public int encode(String message, String URL) throws IOException {
    URL url = new URL(URL);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("PUT");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);

    OutputStream outputStream = connection.getOutputStream();

    try {
      byte[] input = message.getBytes(StandardCharsets.UTF_8);
      outputStream.write(input, 0, input.length);
    } finally {
      outputStream.close();
    }

    return connection.getResponseCode();
  }

  public void handle(String message) {

    try {
      int responseCode = encode(message, queueURL);
      Logger.info("response code = {}, message enqueued successfully", responseCode);
    } catch (IOException e) {
      Logger.error(e);
    }
  }
}
