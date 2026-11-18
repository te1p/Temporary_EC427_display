package edu.bu.metrics.server.handlers;

import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.metrics.MetricsTracker;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UpdateVolumeHandlerTest {
  DataStore dataStore;
  MetricsTracker metricsTracker;
  UpdateVolumeHandler updateVolumeHandler;

  @BeforeEach
  public void setUp() {
    dataStore = new InMemoryStore();
    metricsTracker = new MetricsTracker(dataStore);
    updateVolumeHandler = new UpdateVolumeHandler(metricsTracker);
  }

  @Test
  public void subscribedSymbolsTest() throws IOException {

    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getRequestURI()).thenReturn(URI.create("/updates-volume"));
    when(exchange.getResponseBody()).thenReturn(responseBody);

    updateVolumeHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(200), Mockito.anyLong());
  }
}
