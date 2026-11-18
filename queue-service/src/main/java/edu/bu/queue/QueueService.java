package edu.bu.queue;

import edu.bu.queue.data.DataStoreQueueService;
import edu.bu.queue.data.InMemoryStoreQueueService;
import edu.bu.queue.server.QueueWebServer;
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
public class QueueService {
  static final String MOCK_FINHUB_ARGUMENT = "mockFinhub";

  static final String WEBHOOK_URI = "wss://ws.finnhub.io";
  static final String API_TOKEN = "cq1vjm1r01ql95nces30cq1vjm1r01ql95nces3g";

  // StockAppServer
  public static void main(String[] args) throws IOException, URISyntaxException {
    //    Set<String> arguments = new HashSet<>(List.of(args));
    Logger.info("Starting QueueService with arguments: {}", List.of(args));

    // set up store
    DataStoreQueueService store = new InMemoryStoreQueueService();

    // start web server
    QueueWebServer webServer = new QueueWebServer(store);
    webServer.start();
  }
}
