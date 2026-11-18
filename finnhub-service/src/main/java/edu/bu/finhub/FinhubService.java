package edu.bu.finhub;

// import edu.bu.analytics.CachingAnalyticsComputor;
// import edu.bu.data.DataStore;
// import edu.bu.data.InMemoryStore;
import com.sun.net.httpserver.HttpServer;
// import edu.bu.metrics.MetricsTracker;
// import edu.bu.metrics.server.MetricsWebServer;
import edu.bu.finhub.persistence.SQL_BasedPersistence;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.handlers.CORSHandler;
import edu.bu.server.handlers.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.tinylog.Logger;

/**
 * Entry point for the entire single process StockApp. It initializes the following collaborators:
 * <li>FinHubClient - to register listeners for WebSocket updates from FinHub
 * <li>BasicWebServer - to configure supported HTTP endpoints for our (StockApp) users
 * <li>DataStore - an instance of a data store to store data that we receive from FinHub
 * <li>BasicAnalyticsComputor - business logic around all computations performed by our app
 */
public class FinhubService {
  static final String MOCK_FINHUB_ARGUMENT = "mockFinhub";

  static final String WEBHOOK_URI = "wss://ws.finnhub.io";
  static final String API_TOKEN = "cq1vjm1r01ql95nces30cq1vjm1r01ql95nces3g";

  public static void start(
      StockUpdatesClient stockUpdatesClient, SymbolsPersistence symbolsPersistence)
      throws IOException {
    // Create an HttpServer instance
    HttpServer server = HttpServer.create(new InetSocketAddress(8004), 0);

    // Create handler for subscribe api
    server.createContext(
        "/subscribe",
        new CORSHandler(new SubscribeHandler(stockUpdatesClient, symbolsPersistence)));

    server.createContext(
        "/subscribed-symbols",
        new CORSHandler(new SubscribedSymbolsHandler(stockUpdatesClient, symbolsPersistence)));

    server.createContext(
        "/unsubscribe",
        new CORSHandler(new UnsubscribeHandler(stockUpdatesClient, symbolsPersistence)));

    // Start the server
    server.setExecutor(null); // Use the default executor
    server.start();

    Logger.info("Server is running on port 8004");
  }

  // StockAppServer
  public static void main(String[] args)
      throws IOException, URISyntaxException, InterruptedException {
    Set<String> arguments = new HashSet<>(List.of(args));
    Logger.info("Starting StockAppServer with arguments: {}", List.of(args));

    EnqueueingFinnhubResponseHandler enqueueingHandler = new EnqueueingFinnhubResponseHandler();

    SymbolsPersistence symbolsPersistence = new SQL_BasedPersistence();

    // register FinHub websocket listener, or mock based on argument to support local development
    StockUpdatesClient stockUpdatesClient =
        arguments.contains(MOCK_FINHUB_ARGUMENT)
            ? new MockFinhubClient(enqueueingHandler, arguments)
            : new FinHubWebSocketClient(
                WEBHOOK_URI + "?token=" + API_TOKEN, enqueueingHandler, symbolsPersistence);

    stockUpdatesClient.init();

    // start web server
    start(stockUpdatesClient, symbolsPersistence);
  }
}
