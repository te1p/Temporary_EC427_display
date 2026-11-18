package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.SymbolsPersistence;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class SubscribedSymbolsHandler implements HttpHandler {
  final StockUpdatesClient stockUpdatesClient;
  final SymbolsPersistence symbolsPersistence;

  public SubscribedSymbolsHandler(
      StockUpdatesClient stockUpdatesClient, SymbolsPersistence symbolsPersistence) {
    this.stockUpdatesClient = stockUpdatesClient;
    this.symbolsPersistence = symbolsPersistence;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    Set<String> symbols = stockUpdatesClient.getSubscribedSymbols();

    String response = "StockApp is subscribed to " + symbols.size() + " symbols. ";
    if (!symbols.isEmpty()) {
      response = response + "They are: ";
      response = response + String.join(", ", symbols);
      response = response + ".";
    }

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
