package edu.bu.finhub.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoredSymbols implements SymbolsPersistence {

  private static final Logger log = LoggerFactory.getLogger(StoredSymbols.class);
  final Set<StoredSymbol> storedSymbolsSet = new CopyOnWriteArraySet<>();

  public StoredSymbols() {}

  public void filterOld(Set<StoredSymbol> objectsSet) {
    objectsSet.removeIf(
        storedSymbol -> ChronoUnit.DAYS.between(storedSymbol.createdAt, Instant.now()) > 10);
  }

  @Override
  public void add(String symbol) {
    storedSymbolsSet.add(new StoredSymbol(symbol, Instant.now()));
    try {
      Files.createFile(Path.of("symbol-storage/" + symbol));
    } catch (IOException e) {
      log.error("e: ", e);
    }
  }

  @Override
  public void remove(String symbol) {
    for (StoredSymbol storedSymbol : Set.copyOf(storedSymbolsSet)) {
      if (storedSymbol.symbol.equals(symbol)) {
        storedSymbolsSet.remove(storedSymbol);
      }
    }

    try {
      Files.deleteIfExists(Path.of("symbol-storage/" + symbol));
    } catch (IOException e) {
      log.error("e: ", e);
    }
  }

  @Override
  public Set<StoredSymbol> readAll() {
    try (Stream<Path> paths = Files.walk(Paths.get("symbol-storage"))) {
      paths
          .filter(Files::isRegularFile)
          .forEach(
              path -> {
                try {
                  storedSymbolsSet.add(
                      new StoredSymbol(
                          path.getFileName().toString(),
                          Files.readAttributes(path, BasicFileAttributes.class)
                              .creationTime()
                              .toInstant()));
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });
    } catch (java.io.IOException e) {
      log.error("e: ", e);
    }

    filterOld(storedSymbolsSet);
    return Set.copyOf(storedSymbolsSet);
  }
}
