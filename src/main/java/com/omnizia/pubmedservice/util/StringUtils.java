package com.omnizia.pubmedservice.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  private static final String URL_REGEX = "^(https?|ftp)://[^\\s/$.?#].\\S*$";
  private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
  public static final String EMPTY = "";

  public static boolean isNotBlank(String str) {
    return str != null && !str.isBlank();
  }

  public static boolean isValidURL(String urlString) {
    if (urlString == null || urlString.isBlank()) {
      return false;
    }
    Matcher matcher = URL_PATTERN.matcher(urlString);
    return matcher.matches();
  }

  public static String getStringOrEmpty(Object object) {
    return getStringOrDefault(object,EMPTY);
  }

  public static String removeExtraWhitespace(String input) {
    return input.replaceAll("\\s+", " ");
  }

  public static String getStringOrDefault(Object object, String defaultValue) {
    if (object == null) return defaultValue;
    return object.toString().trim();
  }
}
