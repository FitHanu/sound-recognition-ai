import 'package:csv/csv.dart';
import 'package:flutter/services.dart';

class CsvUtils {

  /**
   * Load a CSV file from the given path.
   * + return: List\<List\<dynamic\>\>
   * + path - the path to the CSV file
   */
  static Future<List<List<dynamic>>> parseCsvFromPath(String path) async {
    if (!path.endsWith('.csv')) {
      throw ArgumentError('Invalid file path. The file must have a .csv extension.');
    }
    String csvString = await rootBundle.loadString(path);
    return parseCsv(csvString);
  }

  /**
   * Parse a CSV string into a list of lists.
   * + return: List\<List\<dynamic\>\>
   * + csvString - the CSV string to parse
   */
  static List<List<dynamic>> parseCsv(String csvString) {
    return const CsvToListConverter().convert(csvString);
  }

  /**
   * Get the shape of the CSV data.
   * + return: [x, y]
   * + x - number of rows
   * + y - number of columns
   */
  static List<int> getShape(List<List<dynamic>> csvData) {
    int numRows = csvData.length;
    int numCols = csvData.isNotEmpty ? csvData[0].length : 0;
    return [numRows, numCols];
  }

  /**
   * Check if the CSV data is empty.
   * + return: true if empty, false otherwise
   */
  static bool isEmtpyCsv(List<List<dynamic>> csvData) {
    if (csvData.isEmpty) {
      return true;
    }
    if (csvData.length == 1) {
      return csvData[0].isEmpty;
    }
    return false;
  }


  // static void main() {
  //   // Example CSV string
  //   String csvString = 'Name,Age,City\nJohn,25,New York\nJane,30,Los Angeles';

  //   // Parse the CSV string
  //   List<List<dynamic>> parsedCsv = parseCsv(csvString);

  //   // Print the parsed CSV
  //   // for (var row in parsedCsv) {
  //   //   print(row);
  //   // }
  // }
}