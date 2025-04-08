import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:path_provider/path_provider.dart';
import 'package:csv/csv.dart';
import 'package:uuid/uuid.dart';
import 'package:intl/intl.dart';
import '../domain/history_record.dart';

class HistoryRepository {
  Future<Directory> get _historyDirectory async {
    final appDir = await getApplicationDocumentsDirectory();
    final historyDir = Directory('${appDir.path}/history');
    if (!await historyDir.exists()) {
      await historyDir.create(recursive: true);
    }
    return historyDir;
  }

  // Get all history records
  Future<List<HistoryRecord>> getAllRecords() async {
    try {
      final historyDir = await _historyDirectory;
      final files =
          await historyDir
              .list()
              .where((entity) => entity is File && entity.path.endsWith('.csv'))
              .toList();

      List<HistoryRecord> records = [];

      for (final file in files) {
        if (file is File) {
          try {
            final record = await _parseHistoryFile(file);
            records.add(record);
          } catch (e) {
            debugPrint('Error parsing file ${file.path}: $e');
          }
        }
      }

      // Sort by timestamp in descending order (newest first)
      records.sort((a, b) => b.timestamp.compareTo(a.timestamp));
      return records;
    } catch (e) {
      debugPrint('Error loading history records: $e');
      return [];
    }
  }

  // Parse a single history file
  Future<HistoryRecord> _parseHistoryFile(File file) async {
    final content = await file.readAsString();
    final rows = const CsvToListConverter().convert(content);

    // Extract metadata from filename: sound_detection_YYYY-MM-DD_HH-MM-SS.csv
    final filename = file.path.split('/').last;
    final dateTimeString = filename
        .replaceAll('sound_detection_', '')
        .replaceAll('.csv', '');
    final dateParts = dateTimeString.split('_');
    final timeParts = dateParts[1].split('-');

    final timestamp = DateTime(
      int.parse(dateParts[0].split('-')[0]), // Year
      int.parse(dateParts[0].split('-')[1]), // Month
      int.parse(dateParts[0].split('-')[2]), // Day
      int.parse(timeParts[0]), // Hour
      int.parse(timeParts[1]), // Minute
      int.parse(timeParts[2]), // Second
    );

    // Extract detected sounds from rows
    List<String> detectedSounds = [];
    int durationInSeconds = 0;

    // Skip header row
    for (int i = 1; i < rows.length; i++) {
      final row = rows[i];
      if (row.length >= 2) {
        final sound = row[0].toString();
        if (!detectedSounds.contains(sound)) {
          detectedSounds.add(sound);
        }

        // Calculate duration based on last timestamp
        if (i == rows.length - 1 && row.length >= 2) {
          durationInSeconds = int.parse(row[1].toString());
        }
      }
    }

    return HistoryRecord(
      id: const Uuid().v4(),
      filename: filename,
      timestamp: timestamp,
      detectedSounds: detectedSounds,
      durationInSeconds: durationInSeconds,
      filePath: file.path,
    );
  }

  // Delete a history record
  Future<bool> deleteRecord(HistoryRecord record) async {
    try {
      final file = File(record.filePath);
      if (await file.exists()) {
        await file.delete();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error deleting record: $e');
      return false;
    }
  }

  // Create mock data
  Future<void> createMockData() async {
    final historyDir = await _historyDirectory;

    // Check if mock files already exist
    final files = await historyDir.list().toList();
    if (files.isNotEmpty) {
      return; // Don't create mock data if files already exist
    }

    // Create 5 mock CSV files
    for (int i = 0; i < 5; i++) {
      final now = DateTime.now().subtract(Duration(days: i));
      final dateStr = DateFormat('yyyy-MM-dd_HH-mm-ss').format(now);
      final filename = 'sound_detection_$dateStr.csv';
      final file = File('${historyDir.path}/$filename');

      // Create CSV content
      List<List<dynamic>> rows = [];

      // Header row
      rows.add(['sound_type', 'timestamp_seconds', 'confidence']);

      // Add mock detected sounds
      final sounds = [
        'scream',
        'glass breaking',
        'siren',
        'gunshot',
        'baby cry',
      ];
      final detectedSounds = [
        sounds[i % sounds.length],
        sounds[(i + 2) % sounds.length],
      ];

      // Add some data rows with timestamps
      int totalSeconds = 0;
      final random = DateTime.now().millisecondsSinceEpoch % 10;
      for (int j = 0; j < 10 + random; j++) {
        totalSeconds += 5 + (j % 3);
        rows.add([
          detectedSounds[j % detectedSounds.length],
          totalSeconds,
          0.75 + (j % 20) / 100,
        ]);
      }

      // Convert to CSV and write to file
      final csv = const ListToCsvConverter().convert(rows);
      await file.writeAsString(csv);
    }
  }
}
