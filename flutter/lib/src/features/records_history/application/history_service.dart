import 'dart:io';
import 'package:flutter/material.dart';
import 'package:share_plus/share_plus.dart';
import '../data/history_repository.dart';
import '../domain/history_record.dart';

class HistoryService extends ChangeNotifier {
  final HistoryRepository _repository = HistoryRepository();
  List<HistoryRecord> _records = [];
  bool _isLoading = false;
  String? _error;

  List<HistoryRecord> get records => _records;
  bool get isLoading => _isLoading;
  String? get error => _error;

  HistoryService() {
    loadRecords();
  }

  Future<void> loadRecords() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      // Create mock data if needed (first run)
      await _repository.createMockData();

      // Load all records
      _records = await _repository.getAllRecords();
    } catch (e) {
      _error = 'Failed to load history records: $e';
      debugPrint(_error);
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> deleteRecord(HistoryRecord record) async {
    _isLoading = true;
    notifyListeners();

    try {
      final success = await _repository.deleteRecord(record);
      if (success) {
        _records.removeWhere((r) => r.id == record.id);
      } else {
        _error = 'Failed to delete record';
      }
    } catch (e) {
      _error = 'Error during deletion: $e';
      debugPrint(_error);
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> shareRecord(HistoryRecord record) async {
    try {
      final file = File(record.filePath);
      if (await file.exists()) {
        await Share.shareXFiles([
          XFile(record.filePath),
        ], text: 'Sound detection history from ${record.formattedDate}');
      } else {
        _error = 'File not found';
        notifyListeners();
      }
    } catch (e) {
      _error = 'Error sharing file: $e';
      debugPrint(_error);
      notifyListeners();
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}
