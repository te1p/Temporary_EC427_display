package edu.bu.finhub.persistence;

import java.util.Set;

public interface SymbolsPersistence {

  void add(String symbol);

  void remove(String symbol);

  Set<StoredSymbol> readAll();
}
