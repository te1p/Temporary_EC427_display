package edu.bu.server.handlers;

import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
// import edu.bu.data.DataStore;
// import edu.bu.data.InMemoryStore;
import edu.bu.finhub.MockFinhubClient;
import edu.bu.finhub.persistence.StoredSymbols;
import edu.bu.finhub.persistence.SymbolsPersistence;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SubscribedSymbolsHandlerTest {
  EnqueueingFinnhubResponseHandler enqueueingFinnhubResponseHandler;
  MockFinhubClient mockFinhubClient;
  SubscribedSymbolsHandler subscribedSymbolsHandler;
  SymbolsPersistence symbolsPersistence;

  @BeforeEach
  public void setUp() {
    enqueueingFinnhubResponseHandler = new EnqueueingFinnhubResponseHandler();
    symbolsPersistence = new StoredSymbols();
    mockFinhubClient = new MockFinhubClient(enqueueingFinnhubResponseHandler, Set.of());
    subscribedSymbolsHandler = new SubscribedSymbolsHandler(mockFinhubClient, symbolsPersistence);
  }

  @Test
  public void subscribedSymbolsTest() throws IOException {

    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribed-symbols"));
    when(exchange.getResponseBody()).thenReturn(responseBody);

    subscribedSymbolsHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(200), Mockito.anyLong());
  }
}
