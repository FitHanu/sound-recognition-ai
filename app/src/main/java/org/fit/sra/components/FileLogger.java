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
    private final String storagePath;
    private CSVPrinter printer;

    /**
     * Create instance
     */
    public FileLogger(String storagePath) throws IOException {
        this.storagePath = storagePath;
        this.renew();
    }

    /**
     * Renew the printer with new file
     */
    public void renew() throws IOException{
        String currentTs = getCurrentTsStr();
        String fileName = this.storagePath + File.pathSeparator + currentTs + ".csv";
        File loggingFile = new File(fileName);
        boolean isAppending = true;
        FileWriter fw = new FileWriter(loggingFile, isAppending);
        this.printer = new CSVPrinter(fw, CSVFormat.DEFAULT);
    }


    /**
     * Append Category as log line to file
     */
    public void append(Category category) throws IOException {
        String time = getCurrentTsStr();
        String label = category.getLabel();
        String score = String.valueOf(category.getScore());
        this.printer.printRecord(time, label, score);
    }

    /**
     * Close printer
     */
    public void close() throws IOException {
        this.printer.close(true);
    }

    /**
     * Get current time as String
     */
    private static String getCurrentTsStr() {
        return String.valueOf(System.currentTimeMillis());
    }


}
