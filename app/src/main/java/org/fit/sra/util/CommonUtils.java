package org.fit.sra.util;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class CommonUtils {

  public static boolean isListNullOrEmpty(List obj) {
    if (obj == null) {
      return true;
    }
    if (obj.isEmpty()) {
      return true;
    }
    return false;
  }


  /**
   * Get current time as String
   */
  public static String getFormattedDatetimeStr(ZonedDateTime time, String format) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return time.format(formatter);
  }

  /**
   * @return ZonedDateTime
   */
  public static ZonedDateTime parseFormattedDatetimeStr(String timeStr, String format) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return ZonedDateTime.parse(timeStr, formatter);
  }

  /**
   * get current datetime with zone
   * @return ZonedDateTime
   */
  public static ZonedDateTime getCurrentDatetimeWithZone() {
    ZoneId currentTimeZoneId = Calendar.getInstance().getTimeZone().toZoneId();
    return ZonedDateTime.now(currentTimeZoneId);
  }

  public static long getDurationInSeconds(ZonedDateTime start, ZonedDateTime end) {

    return Duration.between(start, end).getSeconds();      // safe on API 26+

  }
}
