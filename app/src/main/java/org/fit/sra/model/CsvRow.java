package org.fit.sra.model;

public class CsvRow {
  public final String time;
  public final String label;
  public final String category;
  public final String confidence;

  public CsvRow(String time, String label, String category, String confidence) {
    this.time = time;
    this.label = label;
    this.category = category;
    this.confidence = confidence;
  }
}
