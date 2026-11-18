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

public class SubscribeHandlerTest {
  EnqueueingFinnhubResponseHandler enqueueingFinnhubResponseHandler;
  MockFinhubClient mockFinhubClient;
  //  StockUpdatesClient stockUpdatesClient;
  SubscribeHandler subscribeHandler;
  SymbolsPersistence symbolsPersistence;

  @BeforeEach
  public void setUp() {
    enqueueingFinnhubResponseHandler = new EnqueueingFinnhubResponseHandler();
    mockFinhubClient = new MockFinhubClient(enqueueingFinnhubResponseHandler, Set.of());
    symbolsPersistence = new StoredSymbols();
    subscribeHandler = new SubscribeHandler(mockFinhubClient, symbolsPersistence);
  }

  @Test
  public void subscribeTest() throws IOException {
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);

    subscribeHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(200), Mockito.anyLong());
  }

  @Test
  public void subscribeTest_Fail_NotValidSymbol() throws IOException {
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/$#2"));
    when(exchange.getResponseBody()).thenReturn(responseBody);

    subscribeHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(400), Mockito.anyLong());
  }

  @Test
  public void subscribeTest_Fail_SubLimit() throws IOException {
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);

    // We already start with 3 subscriptions with the MockFinhubServer
    // We only need to add 8 new subsribtions for the fail to fire

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVD0"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVD1"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVD2"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVD3"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVD4"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVD5"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVD6"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVD7"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(409), Mockito.anyLong());
  }

  @Test
  public void subscribeTest_Fail_AlreadySub() throws IOException {
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(409), Mockito.anyLong());
  }
}
