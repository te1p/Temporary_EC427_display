package edu.bu.finhub.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StoredSymbolsTest {
  StoredSymbols storedSymbols;

  @BeforeEach
  public void setUp() {
    storedSymbols = new StoredSymbols();
  }

  @Test
  public void filterOld_standardTest() {
    final Set<StoredSymbol> storedSymbolsSet = new CopyOnWriteArraySet<>();
    Set<String> temp = new HashSet<>();

    storedSymbolsSet.add(
        new StoredSymbol("AAA", ZonedDateTime.now(ZoneOffset.UTC).minusDays(1).toInstant()));
    storedSymbolsSet.add(new StoredSymbol("BBB", Instant.now()));
    storedSymbolsSet.add(new StoredSymbol("CCC", Instant.now()));
    storedSymbolsSet.add(
        new StoredSymbol("DDD", ZonedDateTime.now(ZoneOffset.UTC).minusDays(20).toInstant()));

    storedSymbols.filterOld(storedSymbolsSet);

    storedSymbolsSet.forEach(storedSymbol -> temp.add(storedSymbol.symbol));

    assertEquals(Set.of("AAA", "BBB", "CCC"), temp);
  }

  @Test
  public void filterOld_exact10Test() {
    final Set<StoredSymbol> storedSymbolsSet = new CopyOnWriteArraySet<>();
    Set<String> temp = new HashSet<>();

    storedSymbolsSet.add(
        new StoredSymbol("AAA", ZonedDateTime.now(ZoneOffset.UTC).minusDays(1).toInstant()));
    storedSymbolsSet.add(new StoredSymbol("BBB", Instant.now()));
    storedSymbolsSet.add(
        new StoredSymbol("CCC", ZonedDateTime.now(ZoneOffset.UTC).minusDays(11).toInstant()));
    storedSymbolsSet.add(
        new StoredSymbol("DDD", ZonedDateTime.now(ZoneOffset.UTC).minusDays(10).toInstant()));

    storedSymbols.filterOld(storedSymbolsSet);

    storedSymbolsSet.forEach(storedSymbol -> temp.add(storedSymbol.symbol));

    assertEquals(Set.of("AAA", "BBB", "DDD"), temp);
  }

  @Test
  public void filterOld_emptyTest() {
    final Set<StoredSymbol> storedSymbolsSet = new CopyOnWriteArraySet<>();
    Set<String> temp = new HashSet<>();

    storedSymbols.filterOld(storedSymbolsSet);

    storedSymbolsSet.forEach(storedSymbol -> temp.add(storedSymbol.symbol));

    assertEquals(Set.of(), temp);
  }
}
