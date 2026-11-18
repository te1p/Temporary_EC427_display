package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.UnknownSymbolException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tinylog.Logger;

public class AverageVolumePerSecondHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;

  public AverageVolumePerSecondHandler(AnalyticsComputor analyticsComputor) {
    this.analyticsComputor = analyticsComputor;
  }

  public boolean isValid(String symbol) {
    Pattern pattern = Pattern.compile("\\p{Alnum}{1,5}");

    Matcher matcher = pattern.matcher(symbol);

    return matcher.matches();
  }

  public String output(String symbol) {
    if (symbol.isEmpty()) {
      return "No stock symbol detected\n";
    }

    if (!isValid(symbol)) {
      return "Not a valid symbol\n";
    }

    try {

      return analyticsComputor.averageVolumePerSecond(symbol) + "\n";

    } catch (UnknownSymbolException e) {

      return e.getMessage() + "\n";
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // parse out symbol of interest from URL
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    // String symbol = requestURLParts[requestURLParts.length - 1];
    String symbol = requestURLParts.length < 3 ? "" : requestURLParts[requestURLParts.length - 1];

    String response = output(symbol);

    Logger.info("averageVolumePerSecond request for {}, responding with {}.", symbol, response);

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
