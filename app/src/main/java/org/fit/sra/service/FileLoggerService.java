package org.fit.sra.service;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.fit.sra.DangerLevel;
import org.fit.sra.constant.AppConst;
import org.fit.sra.util.CommonUtils;
import org.tensorflow.lite.support.label.Category;

/**
 * This class is responsible for managing its File Logging object Input: Single line data: time
 * (timestamp), level(0 - 3), event name (YAMnet class name), confident (%) Output format type:
 * - currently for csv only
 * Life cycle:
 * renew() -> append() -> save() -> renew()
 */
public class FileLoggerService {


  /** meta delimiter in filename */
  private static final String DE = "_";

  /** str format: time_create _ highest_severity _ duration */
  private static final String LOG_FILENAME_FORMAT = "%s" + DE + "%s" + DE + "%s";

  /** Session highest severity, back to NONE when init and after `renew()` */
  private DangerLevel sessionHighestSeverity;

  /**
   * Application default storage path
   */
  private final File storagePath;

  /** start timestamp */
  private ZonedDateTime startTime;

  /**
   * temp file for session data
   */
  private File tempFile;

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
    this.sessionHighestSeverity = DangerLevel.NONE;
    this.startTime = CommonUtils.getCurrentDatetimeWithZone();
    String filenameStr = CommonUtils.getFormattedDatetimeStr(this.startTime,
        AppConst.LOG_DATETIME_FORMAT);
    this.tempFile = new File(this.storagePath, filenameStr + "_temp.csv");
    boolean isAppending = true;
    try {
      FileWriter fw = new FileWriter(this.tempFile, isAppending);
      this.csvPrinter = new CSVPrinter(fw, CSVFormat.DEFAULT);
    } catch (IOException exception) {
      Log.e("", "cannot renew log file", exception);
    }

  }

  public void saveLog() {

    // Get formated file name
    String formatedFilename = String.format(
        LOG_FILENAME_FORMAT,

        // Part: Start time
        CommonUtils.getFormattedDatetimeStr(this.startTime, AppConst.LOG_DATETIME_FORMAT),

        // Part: Highest Severity
        this.sessionHighestSeverity.getDisplayName(),

        // Part: Duration in seconds
        CommonUtils.getDurationInSeconds(
            this.startTime,
            CommonUtils.getCurrentDatetimeWithZone()
        )
    );


    File finalFile = new File(this.storagePath, formatedFilename + ".csv");

    try (FileInputStream in = new FileInputStream(this.tempFile);
        FileOutputStream out = new FileOutputStream(finalFile)) {

      byte[] buffer = new byte[1024];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }
    } catch (IOException e) {
      Log.d("", "Cannot save file: " + finalFile.getAbsolutePath());
    }

    // Delete temp file
    this.tempFile.deleteOnExit();
  }



  /**
   * Append Category as log line to file
   */
  public void append(Category category) {
    String time = CommonUtils.getFormattedDatetimeStr(ZonedDateTime.now(),
        AppConst.LOG_DATA_DATETIME_FORMAT);
    String label = category.getLabel();
    String score = String.valueOf(category.getScore());
    DangerLevel severity = categoryService
        .getDangerLevelById(category.getIndex());

    // Update highest severity in session
    if (severity.getValue() > this.sessionHighestSeverity.getValue()) {
      this.sessionHighestSeverity = severity;
    }

    String severityDisplayName = severity.getDisplayName();

    try {
      this.csvPrinter.printRecord(time, severityDisplayName, label, score);
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

}
