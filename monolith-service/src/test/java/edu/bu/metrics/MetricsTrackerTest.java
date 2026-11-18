package edu.bu.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.sun.net.httpserver.HttpExchange;
import edu.bu.analytics.BasicAnalyticsComputor;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.finhub.FinhubResponse;
import edu.bu.server.handlers.PriceHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MetricsTrackerTest {

  static final Instant TEST_TIME = Instant.ofEpochMilli(1722470400000L);

  MetricsTracker metricsTracker;
  DataStore dataStore;
  PriceHandler priceHandler;
  BasicAnalyticsComputor analyticsComputor;

  @BeforeEach
  public void setUp() {
    dataStore = new InMemoryStore();
    metricsTracker = new MetricsTracker(dataStore);
    analyticsComputor = new BasicAnalyticsComputor(dataStore);
    priceHandler = new PriceHandler(analyticsComputor, dataStore);
  }

  @Test
  public void updatesVolume_SingleUpdateOneSymbolTest() {
    String response = "Updates-volume:\nNVDA : 1";
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(response, metricsTracker.updatesVolume());
  }

  @Test
  public void updatesVolume_MultipleUpdatesOneSymbolTest() {
    String response = "Updates-volume:\nNVDA : 3";
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    // third response 13 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    assertEquals(response, metricsTracker.updatesVolume());
  }

  @Test
  public void updatesVolume_MultipleUpdatesMultipleSymbolsTest() {
    String response = "Updates-volume:\nNVDA : 2\nAAPL : 1";
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    // third response 13 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    assertEquals(response, metricsTracker.updatesVolume());
  }

  @Test
  public void updatesVolumeEmptySymbolsTest() {
    String response = "Updates-volume:";
    assertEquals(response, metricsTracker.updatesVolume());
  }

  @Test
  public void PriceVolume_SingleRequestOneSymbolTest() throws IOException {
    String response = "Price-volume:\nNVDA : 1";
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);

    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);
    when(exchange.getRequestURI()).thenReturn(URI.create("/price/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);

    priceHandler.handle(exchange);

    assertEquals(response, metricsTracker.priceVolume());
  }

  @Test
  public void PriceVolume_MultipleRequestOneSymbolTest() throws IOException {
    String response = "Price-volume:\nNVDA : 3";
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);

    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);

    when(exchange.getRequestURI()).thenReturn(URI.create("/price/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    priceHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/price/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    priceHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/price/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    priceHandler.handle(exchange);

    assertEquals(response, metricsTracker.priceVolume());
  }

  @Test
  public void updatesVolume_MultipleRequestsMultipleSymbolsTest() throws IOException {
    String response = "Price-volume:\nNVDA : 2\nAAPL : 1";
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    HttpExchange exchange = mock(HttpExchange.class);

    when(exchange.getRequestURI()).thenReturn(URI.create("/price/AAPL"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    priceHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/price/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    priceHandler.handle(exchange);

    when(exchange.getRequestURI()).thenReturn(URI.create("/price/NVDA"));
    when(exchange.getResponseBody()).thenReturn(responseBody);
    priceHandler.handle(exchange);

    assertEquals(response, metricsTracker.priceVolume());
  }

  @Test
  public void updatesPriceEmptySymbolsTest() {
    String response = "Price-volume:";
    assertEquals(response, metricsTracker.priceVolume());
  }
}
