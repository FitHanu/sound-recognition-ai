import 'package:flutter/foundation.dart';
import 'package:intl/intl.dart';

class HistoryRecord {
  final String id;
  final String filename;
  final DateTime timestamp;
  final List<String> detectedSounds;
  final int durationInSeconds;
  final String filePath;

  HistoryRecord({
    required this.id,
    required this.filename,
    required this.timestamp,
    required this.detectedSounds,
    required this.durationInSeconds,
    required this.filePath,
  });

  String get formattedDate {
    return DateFormat('yyyy-MM-dd HH:mm').format(timestamp);
  }

  String get formattedDuration {
    final minutes = durationInSeconds ~/ 60;
    final seconds = durationInSeconds % 60;
    return '$minutes:${seconds.toString().padLeft(2, '0')}';
  }

  String get detectedSoundsText {
    return detectedSounds.join(', ');
  }

  @override
  String toString() {
    return 'HistoryRecord{id: $id, filename: $filename, timestamp: $timestamp, detectedSounds: $detectedSounds, durationInSeconds: $durationInSeconds}';
  }
}
