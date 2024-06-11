package com.omnizia.pubmedservice.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

  public static final String OMNIZIA_DB_DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS+00";

  public static String getCurrentTimeUTC() {
    return getCurrentTimeUTC(OMNIZIA_DB_DATE_FORMATTER);
  }

  public static String getCurrentTimeUTC(String format) {
    ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return currentTime.format(formatter);
  }
}

// 2023-11-25 11:00:00.000  ==> yyyy-MM-dd HH:mm:ss.SSSXXX
// 2023-11-25 11:00:00+00  ==> yyyy-MM-dd HH:mm:ss'+00'
