package edu.bu.server.handlers;

import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
// import edu.bu.data.DataStore;
// import edu.bu.data.InMemoryStore;
import edu.bu.finhub.MockFinhubClient;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.StoredSymbols;
import edu.bu.finhub.persistence.SymbolsPersistence;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UnsubscribeHandlerTest {
  EnqueueingFinnhubResponseHandler enqueueingFinnhubResponseHandler;
  MockFinhubClient mockFinhubClient;
  StockUpdatesClient stockUpdatesClient;
  SubscribeHandler subscribeHandler;
  UnsubscribeHandler unsubscribeHandler;
  SymbolsPersistence symbolsPersistence;

  @BeforeEach
  public void setUp() {
    enqueueingFinnhubResponseHandler = new EnqueueingFinnhubResponseHandler();
    mockFinhubClient = new MockFinhubClient(enqueueingFinnhubResponseHandler, Set.of());
    symbolsPersistence = new StoredSymbols();
    subscribeHandler = new SubscribeHandler(mockFinhubClient, symbolsPersistence);
    unsubscribeHandler = new UnsubscribeHandler(mockFinhubClient, symbolsPersistence);
  }

  @Test
  public void unsubscribeTest() throws IOException {
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getRequestMethod()).thenReturn("DELETE");

    when(exchange.getRequestURI()).thenReturn(URI.create("/subscribe/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    subscribeHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/unsubscribe/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    unsubscribeHandler.handle(exchange);

    verify(exchange, times(2)).sendResponseHeaders(Mockito.eq(200), Mockito.anyLong());
  }

  @Test
  public void subscribeTest_Fail_NotValidSymbol() throws IOException {
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getRequestMethod()).thenReturn("DELETE");

    when(exchange.getRequestURI()).thenReturn(URI.create("/unsubscribe/$#2"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    unsubscribeHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(400), Mockito.anyLong());
  }

  @Test
  public void subscribeTest_Fail_NoSub() throws IOException {
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getRequestMethod()).thenReturn("DELETE");

    when(exchange.getRequestURI()).thenReturn(URI.create("/unsubscribe/ABC"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    unsubscribeHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(Mockito.eq(409), Mockito.anyLong());
  }
}
