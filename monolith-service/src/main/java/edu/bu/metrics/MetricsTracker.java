package edu.bu.metrics;

import edu.bu.data.DataStore;
import java.util.Map;

public class MetricsTracker {
  final DataStore dataStore;

  /** Construct an instance by providing the {@link DataStore} collaborator. */
  public MetricsTracker(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  // Descending order how?
  public String updatesVolume() {

    Map<String, Integer> counts = dataStore.getMapNumberOfUpdatesBySymbol();
    StringBuilder response = new StringBuilder("Updates-volume:");

    counts.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .forEach(
            entry ->
                response
                    .append("\n")
                    .append(entry.getKey())
                    .append(" : ")
                    .append(entry.getValue()));

    return response.toString();
  }

  public String priceVolume() {

    Map<String, Integer> counts = dataStore.getNumberOfPriceReq();
    StringBuilder response = new StringBuilder("Price-volume:");

    counts.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .forEach(
            entry ->
                response
                    .append("\n")
                    .append(entry.getKey())
                    .append(" : ")
                    .append(entry.getValue()));

    return response.toString();
  }
}
