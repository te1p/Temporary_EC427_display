package edu.bu.queue.server;

import com.sun.net.httpserver.HttpServer;
import edu.bu.queue.data.DataStoreQueueService;
import edu.bu.queue.server.handlers.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.tinylog.Logger;

/**
 * Registers RESTful HTTP endpoints that are supported by the StockApp. These endpoints will be
 * invoked by StockApp users to retrieve analytics computed by StockApp.
 */
public class QueueWebServer {
  final DataStoreQueueService store;

  public QueueWebServer(DataStoreQueueService store) {
    this.store = store;
    ;
  }

  public void start() throws IOException {
    // Create an HttpServer instance
    HttpServer server = HttpServer.create(new InetSocketAddress(8010), 0);

    // Create handler
    server.createContext("/enqueue", new EnqueueHandler(store));

    // Create handler
    server.createContext("/dequeue", new DequeueHandler(store));

    // Start the server
    server.setExecutor(null); // Use the default executor
    server.start();

    Logger.info("Server is running on port 8010");
  }
}
