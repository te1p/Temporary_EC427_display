package edu.bu.metrics.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.metrics.MetricsTracker;
import java.io.IOException;
import java.io.OutputStream;

public class PriceVolumeHandler implements HttpHandler {
  final MetricsTracker metricsTracker;

  public PriceVolumeHandler(MetricsTracker metricsTracker) {
    this.metricsTracker = metricsTracker;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String response;

    response = metricsTracker.priceVolume() + "\n";

    //    Logger.info("Handled price request for {}, responding with {}.", symbol, response);

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
