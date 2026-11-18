package edu.bu.finhub;

import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.server.handlers.EnqueueingFinnhubResponseHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.tinylog.Logger;

/**
 * Handles all interactions with the FinHub server. These include registration of stock symbols of
 * interest and handlers for FinHub responses.
 */
public class FinHubWebSocketClient extends WebSocketClient implements StockUpdatesClient {
  final EnqueueingFinnhubResponseHandler enqueueingHandler;
  final SymbolsPersistence symbolsPersistence;
  private Set<String> subscribedSymbols;

  public FinHubWebSocketClient(
      String serverUri,
      EnqueueingFinnhubResponseHandler enqueueingHandler,
      SymbolsPersistence symbolsPersistence)
      throws URISyntaxException, InterruptedException {
    super(new URI(serverUri));

    this.subscribedSymbols = new ConcurrentSkipListSet<>();
    this.enqueueingHandler = new EnqueueingFinnhubResponseHandler();
    this.symbolsPersistence = symbolsPersistence;

    symbolsPersistence
        .readAll()
        .forEach(storedSymbol -> subscribedSymbols.add(storedSymbol.symbol));
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    Logger.info("Handshake successful");
  }

  @Override
  public void onMessage(String message) {
    Logger.info("Received FinHub message {}", message);

    enqueueingHandler.handle(message);
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    Logger.warn("Closing FinHub client due to {}", reason);
  }

  @Override
  public void onError(Exception exception) {
    Logger.warn("Received FinHub error: {}", exception.getMessage());
  }

  @Override
  public void init() {
    Logger.info("Starting WebSocket based FinHub client");
    try {
      super.connectBlocking();
    } catch (java.lang.InterruptedException e) {
      Logger.error(e);
    }
  }

  @Override
  public void subscribe(String symbol) {
    send("{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}");
    subscribedSymbols.add(symbol);
  }

  @Override
  public void unsubscribe(String symbol) {
    send("{\"type\":\"unsubscribe\",\"symbol\":\"" + symbol + "\"}");
    subscribedSymbols.remove(symbol);
  }

  @Override
  public Set<String> getSubscribedSymbols() {
    return Set.copyOf(subscribedSymbols);
  }

  @Override
  public int getLengthSubscribedSymbols() {
    return subscribedSymbols.size();
  }
}
