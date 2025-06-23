package org.fit.sra.service;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.util.Objects;
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
  public static final String DE = "_";

  /** str format: time_create _ highest_severity _ duration */
  private static final String LOG_FILENAME_FORMAT = "%s" + DE + "%s" + DE + "%s";

  /** Session highest severity, back to NONE when init and after `renew()` */
  private DangerLevel sessionHighestSeverity;

  /**
   * Application default storage path
   */
  private final File storagePath;

  /**
   * Temp storage path
   */
  private final File tmpStoragePath;

  /** start timestamp */
  private ZonedDateTime startTime;

  /**
   * temp file for session data
   */
  private File tmpFile;

  private CSVPrinter csvPrinter;
  private final CategorySeverityFilterService categoryService;

  /**
   * Create instance
   */
  public FileLoggerService(Context context) {

    // Final log path
    this.storagePath = new File(context.getFilesDir(), "logs");
    if (!storagePath.exists()) {
      boolean isSuccess = storagePath.mkdirs();
      if (isSuccess) {
        Log.d("FileLoggerService", this.storagePath + " created successfully");
      } else {
        Log.e("FileLoggerService", "Failed creating :" + this.storagePath);
      }
    }

    // Temp log path
    this.tmpStoragePath = new File(context.getFilesDir(), "tmp_logs");
    if (!this.tmpStoragePath.exists()) {
      boolean isSuccess = tmpStoragePath.mkdirs();
      if (isSuccess) {
        Log.d("FileLoggerService", this.tmpStoragePath + " created successfully");
      } else {
        Log.e("FileLoggerService", "Failed creating :" + this.tmpStoragePath);
      }
    }
    Log.d("", "FileLoggerService initialized with storage path: "
        + this.storagePath.getAbsolutePath());
    Log.d("", "FileLoggerService initialized with temporary storage path: "
        + this.tmpStoragePath.getAbsolutePath());
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
    this.tmpFile = new File(this.tmpStoragePath, filenameStr + "_temp.csv");

    try {
      if (Objects.nonNull(this.csvPrinter)) {
        this.csvPrinter.flush();
      }
      boolean isAppending = true;
      FileWriter fw = new FileWriter(this.tmpFile, isAppending);
      this.csvPrinter = new CSVPrinter(fw, CSVFormat.DEFAULT);
    } catch (IOException exception) {
      Log.e("", "cannot renew log file", exception);
    }
    Log.d("", "Log session renewed, temporary saving to: "
        + this.tmpFile.getAbsolutePath());
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

    // Copy contents in temp file to final file
    File finalFile = new File(this.storagePath, formatedFilename + ".csv");

    try  {
      this.csvPrinter.close(true);
      CopyOption replace = StandardCopyOption.REPLACE_EXISTING;
      Files.copy(this.tmpFile.toPath(), finalFile.toPath(), replace);
    } catch (IOException e) {
      Log.e("", "Cannot save file: " + finalFile.getAbsolutePath(), e);
    }

    this.tmpFile.deleteOnExit();

  }



  /**
   * Append Category as log line to file
   */
  public void append(Category category) {
    if (Objects.isNull(this.tmpFile) || Objects.isNull(this.csvPrinter)) {
      this.renew();
    }

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
}
