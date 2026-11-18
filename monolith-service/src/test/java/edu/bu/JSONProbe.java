package edu.bu;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Probe")
public class JSONProbe {

  static final String PROBE_STRING =
      "{\"data\":" + "[" + "{\"p\":215,\"s\":\"AAPL\",\"t\":1704067200000,\"v\":1}," + "]" + "}";

  static final String PROBE_STRING_NO_P =
      "{\"data\":" + "[" + "{\"s\":\"AAPL\",\"t\":1704067200000,\"v\":1}," + "]" + "}";

  static final String PROBE_STRING_MAL =
      "{\"data\":" + "[" + "{\"p\":215,\"s\":\"AAPL\",\"t\":1704067200000,\"v";

  @Test
  public void jsonProbe() throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject parsed = (JSONObject) parser.parse(PROBE_STRING);
    JSONArray data = (JSONArray) parsed.get("data");
    JSONObject zero = (JSONObject) data.getFirst();
    long p = (long) zero.get("p");

    System.out.println("Price: " + p);
  }

  @Test
  public void jsonProbe_noPrice() throws ParseException {

    JSONParser parser = new JSONParser();
    JSONObject parsed = (JSONObject) parser.parse(PROBE_STRING_NO_P);
    JSONArray data = (JSONArray) parsed.get("data");
    JSONObject zero = (JSONObject) data.getFirst();
    long p = (long) zero.get("p");

    System.out.println("Price: " + p);
  }

  @Test
  public void jsonProbe_forceThrow() throws ParseException {
    // TODO: implement
    JSONParser parser = new JSONParser();
    JSONObject parsed = (JSONObject) parser.parse(PROBE_STRING_MAL);
    JSONArray data = (JSONArray) parsed.get("data");
    JSONObject zero = (JSONObject) data.getFirst();
    long p = (long) zero.get("p");

    System.out.println("Price: " + p);
  }
}
