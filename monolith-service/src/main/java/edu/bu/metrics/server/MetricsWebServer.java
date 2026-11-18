package edu.bu.metrics.server;

import com.sun.net.httpserver.HttpServer;
import edu.bu.data.DataStore;
// import edu.bu.finhub.StockUpdatesClient;
import edu.bu.handlers.CORSHandler;
import edu.bu.metrics.MetricsTracker;
import edu.bu.metrics.server.handlers.PriceVolumeHandler;
import edu.bu.metrics.server.handlers.UpdateVolumeHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.tinylog.Logger;

/**
 * Registers RESTful HTTP endpoints that are supported by the StockApp. These endpoints will be
 * invoked by StockApp users to retrieve analytics computed by StockApp.
 */
public class MetricsWebServer {
  final DataStore store;
  final MetricsTracker metricsTracker;

  public MetricsWebServer(DataStore store, MetricsTracker metricsTracker) {
    this.store = store;
    this.metricsTracker = metricsTracker;
  }

  public void start() throws IOException {
    // Create an HttpServer instance
    HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);

    //         Create handler for volume of updates for all known symbols
    server.createContext(
        "/updates-volume", new CORSHandler(new UpdateVolumeHandler(metricsTracker)));

    //        // Create handler for listing volume of prices of all known symbols
    server.createContext("/price-volume", new CORSHandler(new PriceVolumeHandler(metricsTracker)));

    // Start the server
    server.setExecutor(null); // Use the default executor
    server.start();

    Logger.info("Server is running on port 8001");
  }
}
