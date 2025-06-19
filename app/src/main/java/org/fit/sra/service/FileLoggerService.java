package org.fit.sra.service;

import android.content.Context;
import android.util.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.tensorflow.lite.support.label.Category;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is responsible for managing its File Logging object Input: Single line data: time
 * (timestamp), level(0 - 3), event name (YAMnet class name), confident (%) Output format type:
 * - currently for csv only
 */
public class FileLoggerService {

  /**
   * Application default storage path
   */
  private final File storagePath;
  private CSVPrinter csvPrinter;
  private final CategorySeverityFilterService categoryService;

  /**
   * Create instance
   */
  public FileLoggerService(Context context) {
    this.storagePath = new File(context.getFilesDir(), "logs");
    if (!storagePath.exists()) {
      boolean isSuccess = storagePath.mkdirs();
      if (isSuccess) {
        Log.d("FileLoggerService", this.storagePath + " created successfully");
      } else {
        Log.e("FileLoggerService", "Failed creating :" + this.storagePath);
      }
    }
    this.renew();
    this.categoryService = CategorySeverityFilterService.getTheInstance(context);
  }

  /**
   * Renew the printer with new file
   */
  public void renew() {
    String currentTs = getCurrentTsStr();
    String fileName = currentTs + ".csv";
    File loggingFile = new File(storagePath, fileName);
    boolean isAppending = true;
    try {
      FileWriter fw = new FileWriter(loggingFile, isAppending);
      this.csvPrinter = new CSVPrinter(fw, CSVFormat.DEFAULT);
    } catch (IOException exception) {
        Log.e("", "cannot renew log file", exception);
    }

  }


  /**
   * Append Category as log line to file
   */
  public void append(Category category) {
    String time = getCurrentTsStr();
    String label = category.getLabel();
    String score = String.valueOf(category.getScore());
    String severity = categoryService
        .getDangerLevelById(category.getIndex())
        .getDisplayName();
    try {
      this.csvPrinter.printRecord(time, severity, label, score);
    } catch (IOException exception) {
      Log.e("", "error appending log", exception);
    }
  }

  /**
   * Close printer
   */
  public void close() throws IOException {
    this.csvPrinter.close(true);
  }

  /**
   * Get current time as String
   */
  private static String getCurrentTsStr() {
    return String.valueOf(System.currentTimeMillis());
  }


}
