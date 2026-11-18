package edu.bu.server.handlers;

import static edu.bu.server.handlers.HandlerErrorCheck.isValid;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.SymbolsPersistence;
import java.io.IOException;
import java.io.OutputStream;

public class SubscribeHandler implements HttpHandler {
  final StockUpdatesClient stockUpdatesClient;
  final SymbolsPersistence symbolsPersistence;

  public SubscribeHandler(
      StockUpdatesClient stockUpdatesClient, SymbolsPersistence symbolsPersistence) {
    this.stockUpdatesClient = stockUpdatesClient;
    this.symbolsPersistence = symbolsPersistence;
  }

  //    public boolean output(String symbol) {
  //        return !symbol.isEmpty() && isValid(symbol);
  //    }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    //        String response = analyticsComputor.mostActiveStock() + "\n";
    // parse out symbol of interest from URL
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts.length < 3 ? "" : requestURLParts[requestURLParts.length - 1];
    String response = "";

    if (!isValid(symbol)) {
      response = symbol + "is not a valid US Stock symbol";
      exchange.sendResponseHeaders(400, response.length());
    } else if (stockUpdatesClient.getLengthSubscribedSymbols() >= 10) {
      response =
          symbol + "cannot be subscribed to because the server is at its limit of 10 subscriptions";
      exchange.sendResponseHeaders(409, response.length());
    } else if (stockUpdatesClient.getSubscribedSymbols().contains(symbol)) {
      response = symbol + "has already been registered";
      exchange.sendResponseHeaders(409, response.length());
    } else {
      response = "StockApp is now subscribed to updates for " + symbol;
      exchange.sendResponseHeaders(200, response.length());
      stockUpdatesClient.subscribe(symbol);
      symbolsPersistence.add(symbol);
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
