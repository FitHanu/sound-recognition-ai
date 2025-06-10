package org.fit.sra.util;

import org.tensorflow.lite.support.label.Category;

import java.util.List;

public class CategoryUtils {

  /**
   * Convert from list to String for displaying (experimental)
   *
   * @param categories List<Category>
   * @return String
   */
  public static String convertString(List<Category> categories) {
    if (CommonUtils.isListNullOrEmpty(categories)) {
      return "";
    }
    StringBuilder outputStr = new StringBuilder();
    for (Category cat : categories) {
      outputStr.append(cat.getLabel()).append(": ").append(cat.getScore()).append("\n");
    }
    return outputStr.toString();
  }

}
