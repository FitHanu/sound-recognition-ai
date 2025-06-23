package org.fit.sra.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

  public static boolean isArrayNullOrEmpty(Object[] arr) {
    if (arr == null) {
      return true;
    }
    if (arr.length == 0) {
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
    LocalDateTime dt = LocalDateTime.parse(timeStr, formatter);
    return dt.atZone(ZoneId.systemDefault());
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

  /**
   *
   * @param totalSeconds
   * @return
   */
  public static String getFormatedDuration(long totalSeconds) {
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;

    String format = "%02d:%02d:%02d";

    return String.format(Locale.getDefault(), format, hours, minutes, seconds);
  }

  public static String getMomentSoundEventText(String label) {
    return "In moment sound event: " + label;
  }
}
