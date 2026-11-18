package edu.bu.queue.server.handlers;

import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.queue.data.DataStoreQueueService;
import edu.bu.queue.data.InMemoryStoreQueueService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DequeueHandlerTest {
  DataStoreQueueService dataStore;
  DequeueHandler dequeueHandler;

  @BeforeEach
  public void setUp() {
    dataStore = new InMemoryStoreQueueService();
    dequeueHandler = new DequeueHandler(dataStore);
  }

  @Test
  public void dequeueTest_standard() throws IOException {
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getResponseBody()).thenReturn(responseBody);

    dataStore.updateRequestQueue("'{/name:Test Message/value:42}/'");
    dequeueHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(200), Mockito.anyLong());
  }

  @Test
  public void dequeueTest_empty() throws IOException {
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getResponseBody()).thenReturn(responseBody);

    dequeueHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(204), Mockito.anyLong());
  }
}
