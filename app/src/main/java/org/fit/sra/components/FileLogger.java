package org.fit.sra.components;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.tensorflow.lite.support.label.Category;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is responsible for managing its File Logging object
 * Input:
 *  Single line data: time (timestamp), level(0 - 3), event name (YAMnet class name), confident (%)
 * Output format type:
 * - currently for csv only
 *
 */
public class FileLogger {

    /**
     * Application default storage path
     */
    private final File storagePath;
    private CSVPrinter csvPrinter;

    /**
     * Create instance
     */
    public FileLogger(File storagePath) {
        this.storagePath = storagePath;
        this.renew();
    }

    /**
     * Renew the printer with new file
     */
    public void renew() {
        String currentTs = getCurrentTsStr();
        String fileName = this.storagePath + File.pathSeparator + currentTs + ".csv";
        File loggingFile = new File(fileName);
        boolean isAppending = true;
        try {
            FileWriter fw = new FileWriter(loggingFile, isAppending);
            this.csvPrinter = new CSVPrinter(fw, CSVFormat.DEFAULT);
        } catch (IOException exception) {
            //TODO: do something
        }

    }


    /**
     * Append Category as log line to file
     */
    public void append(Category category) {
        String time = getCurrentTsStr();
        String label = category.getLabel();
        String score = String.valueOf(category.getScore());
        try {
            this.csvPrinter.printRecord(time, label, score);
        } catch (IOException exception) {
            //TODO: do something
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
