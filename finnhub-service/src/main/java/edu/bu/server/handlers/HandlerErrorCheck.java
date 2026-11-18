package edu.bu.server.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandlerErrorCheck {

  public static boolean isValid(String symbol) {
    Pattern pattern = Pattern.compile("\\p{Alnum}{1,5}");

    Matcher matcher = pattern.matcher(symbol);

    return !symbol.isEmpty() && matcher.matches();
  }
}
