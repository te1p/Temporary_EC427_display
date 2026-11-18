package edu.bu.finhub.persistence;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.tinylog.Logger;

public class SQL_BasedPersistence implements SymbolsPersistence {

  final String SQL_URL = "jdbc:sqlite:symbolpersistence.db";
  final Set<StoredSymbol> storedSymbolsSet = new CopyOnWriteArraySet<>();
  Connection connection = null;

  public void filterOld(Set<StoredSymbol> objectsSet) {
    objectsSet.removeIf(
        storedSymbol -> ChronoUnit.DAYS.between(storedSymbol.createdAt, Instant.now()) > 10);
  }

  public SQL_BasedPersistence() {
    try {
      connection = DriverManager.getConnection(SQL_URL);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
          Logger.error(e);
        }
      }
    }
  }

  @Override
  public void add(String symbol) {
    String time = Instant.now().toString();
    String insert_SQL =
        "INSERT INTO symbolStorage(symbol, createdAt) VALUES(" + symbol + ", " + time + ");";
    try (PreparedStatement preparedStatement = connection.prepareStatement(insert_SQL)) {
      preparedStatement.executeUpdate();
    } catch (java.sql.SQLException e) {
      Logger.error(e);
    }
  }

  @Override
  public void remove(String symbol) {
    String delete_SQL = "DELETE FROM symbolStorage WHERE symbol='" + symbol + "';";
    try (PreparedStatement preparedStatement = connection.prepareStatement(delete_SQL)) {
      preparedStatement.executeUpdate();
    } catch (java.sql.SQLException e) {
      Logger.error(e);
    }
  }

  @Override
  public Set<StoredSymbol> readAll() {
    storedSymbolsSet.clear();
    String query = "SELECT symbol, createdAt from symbolStorage;";
    try (Statement stmt = connection.createStatement()) {
      try (ResultSet ruleset = stmt.executeQuery(query)) {
        while (ruleset.next()) {
          String symbol = ruleset.getString("symbol");
          Instant createdAt = Instant.ofEpochSecond(ruleset.getInt("createdAt"));

          storedSymbolsSet.add(new StoredSymbol(symbol, createdAt));
        }
      }
    } catch (SQLException e) {
      Logger.error(e);
    }

    filterOld(storedSymbolsSet);
    return Set.copyOf(storedSymbolsSet);
  }
}
