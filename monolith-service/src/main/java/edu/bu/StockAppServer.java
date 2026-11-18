package edu.bu;

import edu.bu.analytics.CachingAnalyticsComputor;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.finhub.FinhubParser;
import edu.bu.metrics.MetricsTracker;
import edu.bu.metrics.server.MetricsWebServer;
import edu.bu.server.BasicWebServer;
import edu.bu.server.QueueReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.tinylog.Logger;

/**
 * Entry point for the entire single process StockApp. It initializes the following collaborators:
 * <li>FinHubClient - to register listeners for WebSocket updates from FinHub
 * <li>BasicWebServer - to configure supported HTTP endpoints for our (StockApp) users
 * <li>DataStore - an instance of a data store to store data that we receive from FinHub
 * <li>BasicAnalyticsComputor - business logic around all computations performed by our app
 */
public class StockAppServer {
  static final String MOCK_FINHUB_ARGUMENT = "mockFinhub";

  static final String WEBHOOK_URI = "wss://ws.finnhub.io";
  static final String API_TOKEN = "cq1vjm1r01ql95nces30cq1vjm1r01ql95nces3g";

  // StockAppServer
  public static void main(String[] args) throws IOException, URISyntaxException {
    //    Set<String> arguments = new HashSet<>(List.of(args));
    Logger.info("Starting StockAppServer with arguments: {}", List.of(args));

    // set up store
    DataStore store = new InMemoryStore();

    // set up analytics computations
    CachingAnalyticsComputor analyticsComputor = new CachingAnalyticsComputor((store));

    // set up metrics tracker
    MetricsTracker metricsTracker = new MetricsTracker(store);

    // set up finnhub parser
    FinhubParser finhubParser = new FinhubParser();

    // register FinHub websocket listener, or mock based on argument to support local development
    //    StockUpdatesClient stockUpdatesClient =
    //        arguments.contains(MOCK_FINHUB_ARGUMENT)
    //            ? new MockFinhubClient(store, arguments)
    //            : new FinHubWebSocketClient(WEBHOOK_URI + "?token=" + API_TOKEN, store);

    //    stockUpdatesClient.connect();

    // start web server
    BasicWebServer webServer = new BasicWebServer(store, analyticsComputor);
    webServer.start();
    MetricsWebServer metricsWebServer = new MetricsWebServer(store, metricsTracker);
    metricsWebServer.start();
    QueueReader queueReader = new QueueReader(finhubParser, store);

    new Thread(
            () -> {
              int sleepTime;
              while (true) {
                try {
                  sleepTime = queueReader.handle();
                  Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
              }
            })
        .start();
  }
}
