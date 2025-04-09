import 'package:csv/csv.dart';
import 'package:flutter/services.dart';

List<List<dynamic>> parseCsvFromString(String csvString) {
  return CsvToListConverter().convert(csvString);
}

Future<List<List>> parseCsvFromAssets(String assetPath) async {
  final csvString = await rootBundle.loadString(assetPath);
  return parseCsvFromString(csvString);
}

List<int> getShape(List<List<dynamic>> data) {
  if (data.isEmpty) {
    return [0, 0];
  }
  int rows = data.length;
  int columns = data[0].length;
  return [rows, columns];
}

List<dynamic> getCsvRow(List<List<dynamic>> data, int rowIndex) {
  return data[rowIndex];
}

List<dynamic> getCsvColumn(List<List<dynamic>> data, int colIndex) {
  return data.map((row) => row[colIndex]).toList();
}