package edu.bu.queue.server.handlers;

// import com.sun.net.httpserver.HttpExchange;
import edu.bu.queue.data.DataStoreQueueService;

// import edu.bu.queue.data.InMemoryStoreQueueService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
//
// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.net.URI;
// import java.nio.charset.StandardCharsets;
// import java.util.Set;
//
// import static org.mockito.Mockito.*;

public class EnqueueHandlerTest {
  DataStoreQueueService dataStore;
  EnqueueHandler enqueueHandler;

  //    @BeforeEach
  //    public void setUp() {
  //        dataStore = new InMemoryStoreQueueService();
  //        enqueueHandler = new EnqueueHandler(dataStore);
  //    }
  //
  //    @Test
  //    public void enqueueTest_standard() throws IOException {
  //        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
  //        HttpExchange exchange = mock(HttpExchange.class);
  //        when(exchange.getRequestMethod()).thenReturn("PUT");
  //        when(new String(exchange.getRequestBody().readAllBytes(),
  // StandardCharsets.UTF_8)).thenReturn("{/name:Test Message/value:42}/");
  //        when(exchange.getResponseBody()).thenReturn(responseBody);
  //
  //        enqueueHandler.handle(exchange);
  //
  //        verify(exchange).sendResponseHeaders(Mockito.eq(200), Mockito.anyLong());
  //    }
  //
  //    @Test
  //    public void enqueueTest_empty() throws IOException {
  //        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
  //        HttpExchange exchange = mock(HttpExchange.class);
  //        when(exchange.getRequestMethod()).thenReturn("PUT");
  //        when(new String(exchange.getRequestBody().readAllBytes(),
  // StandardCharsets.UTF_8)).thenReturn("");
  //        when(exchange.getResponseBody()).thenReturn(responseBody);
  //
  //        enqueueHandler.handle(exchange);
  //
  //        verify(exchange).sendResponseHeaders(Mockito.eq(200), Mockito.anyLong());
  //    }

}
