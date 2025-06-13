package org.fit.sra.util;

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
}
