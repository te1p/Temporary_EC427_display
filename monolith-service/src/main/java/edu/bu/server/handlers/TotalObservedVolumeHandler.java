package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.UnknownSymbolException;
import edu.bu.data.DataStore;
import java.io.IOException;
import java.io.OutputStream;
import org.tinylog.Logger;

/** Handler for price updates arriving via WebSocket callbacks from Finhub. */
public class TotalObservedVolumeHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;
  final DataStore dataStore;

  public TotalObservedVolumeHandler(AnalyticsComputor analyticsComputor, DataStore dataStore) {
    this.analyticsComputor = analyticsComputor;
    this.dataStore = dataStore;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // parse out symbol of interest from URL
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];

    String response;
    try {
      response = analyticsComputor.totalObservedVolume(symbol) + "\n";
      //            dataStore.updateNumberOfPriceReq(symbol);
    } catch (UnknownSymbolException e) {
      response = e.getMessage() + "\n";
    }

    Logger.info("Handled total volume request for {}, responding with {}.", symbol, response);

    exchange.sendResponseHeaders(200, response.length());

    //    OutputStream outputStream = exchange.getResponseBody();
    //    outputStream.write(response.getBytes());
    OutputStream outputStream = null;

    try {
      outputStream = exchange.getResponseBody();
      outputStream.write(response.getBytes());
    } finally {
      outputStream.close();
    }
  }
}
