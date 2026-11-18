package edu.bu.analytics;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableList;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicAnalyticsComputorTest {

  static final Instant TEST_TIME = Instant.ofEpochMilli(1722470400000L);

  AnalyticsComputor analyticsComputor;
  DataStore dataStore;

  @BeforeEach
  public void setUp() {
    dataStore = new InMemoryStore();
    analyticsComputor = new BasicAnalyticsComputor(dataStore);
  }

  @Test
  public void currentPrice_unknownSymbol() throws UnknownSymbolException {
    UnknownSymbolException exception =
        assertThrows(UnknownSymbolException.class, () -> analyticsComputor.currentPrice("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void currentPrice_singleValue() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(134.12, analyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void currentPrice_multipleValues() throws UnknownSymbolException {
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

    assertEquals(135.01, analyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void totalObservedVolume_uknownSymbol() throws UnknownSymbolException {
    UnknownSymbolException exception =
        assertThrows(
            UnknownSymbolException.class, () -> analyticsComputor.totalObservedVolume("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void totalObservedVolume_oneDataPoint() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(100, analyticsComputor.totalObservedVolume("NVDA"), 0.01);
  }

  @Test
  public void totalObservedVolume_multipleMixedDataPoints() throws UnknownSymbolException {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 50)));

    // third response 13 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 70)));

    assertEquals(170, analyticsComputor.totalObservedVolume("NVDA"), 0.01);
  }

  @Test
  public void mostActiveStock_noData() {
    assertNull(analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_singleDataPoint() {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals("NVDA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeNotMostDataPoints() {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 200)));

    // third response 13 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 10)));

    assertEquals("AAPL", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeAlsoMostDataPoints() {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 200)));

    // third response 13 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 130)));

    assertEquals("NVDA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStockInWindowOnBoundary() {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 200)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    // third response 15 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(15, ChronoUnit.SECONDS).toEpochMilli(), 10)));

    assertEquals(
        "AAPL",
        analyticsComputor.mostActiveStock(
            TEST_TIME.plus(5, ChronoUnit.SECONDS), TEST_TIME.plus(15, ChronoUnit.SECONDS)));
  }

  @Test
  public void mostActiveStockInWindowNotBoundary() {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 200)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    // third response 15 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(15, ChronoUnit.SECONDS).toEpochMilli(), 10)));

    // fourth response 25 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(25, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    assertEquals(
        "AAPL",
        analyticsComputor.mostActiveStock(
            TEST_TIME.plus(4, ChronoUnit.SECONDS), TEST_TIME.plus(20, ChronoUnit.SECONDS)));
  }

  @Test
  public void mostActiveStockInWindowNoData() {
    assertNull(
        analyticsComputor.mostActiveStock(
            TEST_TIME.plus(4, ChronoUnit.SECONDS), TEST_TIME.plus(20, ChronoUnit.SECONDS)));
  }

  @Test
  public void mostActiveStockNoTradeInWindow() {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 200)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    // third response 15 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(15, ChronoUnit.SECONDS).toEpochMilli(), 10)));

    assertNull(
        analyticsComputor.mostActiveStock(
            TEST_TIME.plus(17, ChronoUnit.SECONDS), TEST_TIME.plus(25, ChronoUnit.SECONDS)));
  }

  @Test
  public void averageVolumePerSecond_standard() throws UnknownSymbolException {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 300)));

    // second response
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.33, TEST_TIME.plus(200000, ChronoUnit.MILLIS).toEpochMilli(), 100)));

    // third response
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(400000, ChronoUnit.MILLIS).toEpochMilli(), 400)));

    assertEquals(2.0, analyticsComputor.averageVolumePerSecond("NVDA"));
  }

  @Test
  public void averageVolumePerSecond_oneInput() throws UnknownSymbolException {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 90.18, TEST_TIME.toEpochMilli(), 300)));

    assertEquals(300.0, analyticsComputor.averageVolumePerSecond("NVDA"));
  }

  @Test
  public void knownSymbols_empty() {
    assertTrue(analyticsComputor.knownSymbols().isEmpty());
  }

  @Test
  public void knownSymbols_single() {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));
    Set<String> answer = new HashSet<>();
    answer.add("NVDA");

    dataStore.update(singleResponse);
    assertEquals(answer, analyticsComputor.knownSymbols());
  }

  @Test
  public void knownSymbols_multiple() {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 200)));

    // third response 13 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "TSLA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 130)));

    assertEquals(Set.of("NVDA", "AAPL", "TSLA"), analyticsComputor.knownSymbols());
  }
}
