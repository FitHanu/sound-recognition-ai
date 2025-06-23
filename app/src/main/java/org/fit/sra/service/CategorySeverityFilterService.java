package org.fit.sra.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.fit.sra.DangerLevel;
import org.fit.sra.constant.AppConst;
import android.util.Log;
import android.content.Context;


/**
 * CategorySeverityFilterService
 */
public class CategorySeverityFilterService {

  /** the .csv config file path */
  private final String modelConfigFilePath = AppConst.DEFAULT_CONFIG_CSV;

  /** singleton instance */
  private static CategorySeverityFilterService THE_INSTANCE;

  /** privey constructor */
  private CategorySeverityFilterService(Context context) {
    initialize(context);
  }

  /** Map: category_id -> severity */
  private final Map<Integer, DangerLevel> idDangerLevelMap = new HashMap<>();

  /** Map: category_id -> display name */
  private final Map<Integer, String> idAlternateClassnameMap = new HashMap<>();

  /** singleton instance getter*/
  public static CategorySeverityFilterService getTheInstance(Context context) {
    if (Objects.isNull(THE_INSTANCE)) {
      THE_INSTANCE = new CategorySeverityFilterService(context);
    }
    return THE_INSTANCE;
  }

  private void initialize(Context context) {
    try {
      Log.d("CSV", "Reading from assets...");
      InputStream is = context.getAssets().open(modelConfigFilePath);
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String line;
      boolean firstLine = true;
      while ((line = reader.readLine()) != null) {
        if (firstLine) {
          firstLine = false; // skip header
          continue;
        }
        String[] tokens = line.split(",");
        int index = Integer.parseInt(tokens[0].trim()); // e.g. "0"
        String label = tokens[1].trim();               // e.g. "Explosion"
        boolean isDisplaying = Objects.equals(tokens[3].trim(), "1");
        if (isDisplaying) {
          String severity = tokens[2].trim();
          String displayLabel = tokens[4].trim();
          this.idAlternateClassnameMap.put(index, displayLabel);
          this.idDangerLevelMap.put(index, DangerLevel.createFromStr(severity));
        }
      }
      reader.close();
    } catch (IOException e) {
      Log.e("CSV", "Failed to read CSV", e);
    }
  }

  public String getAlternateClassNameById(Integer id) {
    return this.idAlternateClassnameMap.getOrDefault(id, "SILENCE");
  }

  public DangerLevel getDangerLevelById(Integer id) {
    return this.idDangerLevelMap.getOrDefault(id, DangerLevel.NONE);
  }
}
