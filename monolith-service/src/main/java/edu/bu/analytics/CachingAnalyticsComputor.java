package edu.bu.analytics;

import edu.bu.data.DataStore;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CachingAnalyticsComputor implements AnalyticsComputor {

  final BasicAnalyticsComputor basicAnalyticsComputor;
  final DataStore dataStore;

  /** Stock symbol keyed hash of total observed volume results. */
  Map<String, Long> totalObservedVolumeCache = new HashMap<>();

  public CachingAnalyticsComputor(DataStore dataStore) {
    this.dataStore = dataStore;
    this.basicAnalyticsComputor = new BasicAnalyticsComputor(dataStore);
  }

  int prevNumberOfUpdates = 0;

  // Leaving the rest for you to implement, but here is an example of how to delegate
  // to a method that is not one that we will be using the cache for:
  @Override
  public Set<String> knownSymbols() {
    return basicAnalyticsComputor.knownSymbols();
  }

  @Override
  public long totalObservedVolume(String symbol) throws UnknownSymbolException {
    int nextNumberOfUpdates = dataStore.numberOfUpdatesSeen();

    if (nextNumberOfUpdates != prevNumberOfUpdates) {
      totalObservedVolumeCache.clear();
      prevNumberOfUpdates = nextNumberOfUpdates;
    }

    if (!totalObservedVolumeCache.containsKey(symbol)) {
      long totalVolume = basicAnalyticsComputor.totalObservedVolume(symbol);
      totalObservedVolumeCache.put(symbol, totalVolume);
      return totalVolume;
    } else {
      return totalObservedVolumeCache.get(symbol);
    }
  }

  @Override
  public String mostActiveStock() {
    return basicAnalyticsComputor.mostActiveStock();
  }

  @Override
  public String mostActiveStock(Instant startTime, Instant endTime) {
    return basicAnalyticsComputor.mostActiveStock(startTime, endTime);
  }

  @Override
  public double averageVolumePerSecond(String symbol) throws UnknownSymbolException {
    return basicAnalyticsComputor.averageVolumePerSecond(symbol);
  }

  @Override
  public double currentPrice(String symbol) throws UnknownSymbolException {
    return basicAnalyticsComputor.currentPrice(symbol);
  }
}
