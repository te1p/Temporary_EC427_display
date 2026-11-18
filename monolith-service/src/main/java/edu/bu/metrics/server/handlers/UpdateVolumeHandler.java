package edu.bu.metrics.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.metrics.MetricsTracker;
import java.io.IOException;
import java.io.OutputStream;

public class UpdateVolumeHandler implements HttpHandler {
  final MetricsTracker metricsTracker;

  public UpdateVolumeHandler(MetricsTracker metricsTracker) {
    this.metricsTracker = metricsTracker;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    String response;
    response = metricsTracker.updatesVolume() + "\n";

    exchange.sendResponseHeaders(200, response.length());

    OutputStream outputStream = null;

    try {
      outputStream = exchange.getResponseBody();
      outputStream.write(response.getBytes());
    } finally {
      outputStream.close();
    }
  }
}
