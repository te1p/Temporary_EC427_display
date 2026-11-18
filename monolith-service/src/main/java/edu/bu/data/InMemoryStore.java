package edu.bu.data;

import edu.bu.finhub.FinhubResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A data store implementation that keeps all data in memory only. This implies that data is not
 * persisted across server restarts.
 */
public class InMemoryStore implements DataStore {
  final Map<String, Stack<FinhubResponse>> responsesBySymbol;

  // thread safe integer ensures that processes can be calling update and incrementing this count
  // while others are concurrently calling numberOfUpdatesSeen and this will behave as expected
  final AtomicInteger numberOfUpdates = new AtomicInteger(0);
  final Map<String, Integer> numberOfPriceReqBySymbol = new ConcurrentHashMap<>();
  final Map<String, Integer> numberOfUpdatesBySymbol = new ConcurrentHashMap<>();

  public InMemoryStore() {
    this.responsesBySymbol = new ConcurrentHashMap<>();
    // added a diamond
  }

  @Override
  public void update(List<FinhubResponse> responses) {
    for (FinhubResponse response : responses) {
      responsesBySymbol
          .computeIfAbsent(response.symbol.toUpperCase(), key -> new Stack<>())
          .push(response);
      // For each response in the List we check for its symbol and either increment number of
      // updates seen
      // or add the symbol as new key and set value to 1
      numberOfUpdatesBySymbol.merge(response.symbol.toUpperCase(), 1, Integer::sum);
    }

    numberOfUpdates.incrementAndGet();
  }

  @Override
  public int numberOfUpdatesSeen() {
    return numberOfUpdates.get();
  }

  @Override
  public Integer numberOfUpdatesSeenBySymbol(String symbol) {
    return numberOfUpdatesBySymbol.get(symbol);
  }

  @Override
  public Map<String, Integer> getMapNumberOfUpdatesBySymbol() {
    return Map.copyOf(numberOfUpdatesBySymbol);
  }

  @Override
  public void updateNumberOfPriceReq(String symbol) {
    numberOfPriceReqBySymbol.merge(symbol.toUpperCase(), 1, Integer::sum);
  }

  @Override
  public Integer getNumberOfPriceReqBySymbol(String symbol) {
    return numberOfPriceReqBySymbol.get(symbol);
  }

  @Override
  public Map<String, Integer> getNumberOfPriceReq() {
    return Map.copyOf(numberOfPriceReqBySymbol);
  }

  @Override
  public List<FinhubResponse> getHistory(String symbol) {
    // by default when a list is created from a stack, it reverses the order, we want to preserve
    // stack order here
    List<FinhubResponse> history = new ArrayList<>(responsesBySymbol.get(symbol.toUpperCase()));
    Collections.reverse(history);

    return history;
  }

  @Override
  public Set<String> knownSymbols() {
    return new HashSet<>(responsesBySymbol.keySet());
  }

  @Override
  public boolean haveSymbol(String symbol) {
    return responsesBySymbol.containsKey(symbol.toUpperCase());
  }
}
