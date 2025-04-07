import 'package:danger_sound_recognition/src/utils/csv_utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'dart:developer';

class DebuggingAndTesting extends StatefulWidget {
  const DebuggingAndTesting({super.key});

  @override
  State<DebuggingAndTesting> createState() => _DebuggingAndTestingState();
}

class _DebuggingAndTestingState extends State<DebuggingAndTesting> {
  List<List<dynamic>> _csvData = [];

  Future<void> loadCsv() async {
    final csvString = await rootBundle.loadString('assets/models/yamnet_tweaked/classes_default_config.csv');
    List<List<dynamic>> csvTable = CsvUtils.parseCsv(csvString); 
    
    setState(() {
      _csvData = csvTable;
    });
  }

  @override
  void initState() {
    super.initState();
    loadCsv();
  }

  @override
  Widget build(BuildContext context) {
    List<int> shape = CsvUtils.getShape(_csvData);
    int x = shape[0];
    int y = shape[1];
    List<String> columnNames = _csvData[0].map((e) => e.toString()).toList();
    return Scaffold(
      appBar: AppBar(
        title: Text('Subpage for Debugging, Testing'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Welcome to the Subpage!',
              style: TextStyle(fontSize: 24),
            ),
            SizedBox(height: 20),
            Text(
              'This page is used for debugging and testing routes.',
              style: TextStyle(fontSize: 18),
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context); // Go back to the home page
              },
              child: Text('Go Back'),
            ),

            if (x > 0 && y > 0)
              DataTable(
              columns: List.generate(columnNames.length, (index) {
                return DataColumn(
                label: Text(columnNames[index]),
                );
              }),
              rows: List.generate(
                x,
                (index) {
                return DataRow(
                  cells: List.generate(y, (index2) {
                  return DataCell(
                    Text(_csvData[index][index2].toString()),
                  );
                  }),
                );
                },
              ),
              )
            else
              Text(
              'No data available',
              style: TextStyle(fontSize: 18, color: Colors.red),
              ),

          ],
        ),
      ),
    );
  }
}