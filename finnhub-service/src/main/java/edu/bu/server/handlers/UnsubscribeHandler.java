package edu.bu.server.handlers;

import static edu.bu.server.handlers.HandlerErrorCheck.isValid;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.SymbolsPersistence;
import java.io.IOException;
import java.io.OutputStream;

public class UnsubscribeHandler implements HttpHandler {
  final StockUpdatesClient stockUpdatesClient;
  final SymbolsPersistence symbolsPersistence;

  public UnsubscribeHandler(
      StockUpdatesClient stockUpdatesClient, SymbolsPersistence symbolsPersistence) {
    this.stockUpdatesClient = stockUpdatesClient;
    this.symbolsPersistence = symbolsPersistence;
  }

  //    public boolean output(String symbol) {
  //        return !symbol.isEmpty() && isValid(symbol);
  //    }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts.length < 3 ? "" : requestURLParts[requestURLParts.length - 1];
    String response = "";

    if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
      response = "Method Not Allowed";
      exchange.sendResponseHeaders(405, response.length());
    } else if (!isValid(symbol)) {
      response = symbol + "is not a valid US Stock symbol";
      exchange.sendResponseHeaders(400, response.length());
    } else if (!stockUpdatesClient.getSubscribedSymbols().contains(symbol)) {
      response = symbol + "has not been previously subscribed to";
      exchange.sendResponseHeaders(409, response.length());
    } else {
      response = "StockApp is now unsubscribed from updates for " + symbol;
      exchange.sendResponseHeaders(200, response.length());
      stockUpdatesClient.unsubscribe(symbol);
      symbolsPersistence.remove(symbol);
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
