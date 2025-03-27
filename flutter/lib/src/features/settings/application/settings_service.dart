import 'package:flutter/material.dart';
import '../data/settings_repository.dart';
import '../domain/settings.dart';

class SettingsService extends ChangeNotifier {
  final SettingsRepository _repository;

  bool _alertSound = false;
  bool get alertSound => _alertSound;

  SettingsService(this._repository);

  Future<void> loadSettings() async {
    final settings = await _repository.loadSettings();
    _alertSound = settings.alertSound;

    // Initialize default if not found
    if (!settings.isInitialized) {
      await updateAlertSound(false);
    }

    notifyListeners();
  }

  Future<void> updateAlertSound(bool value) async {
    _alertSound = value;
    await _repository.saveSettings(Settings(alertSound: value, isInitialized: true));
    notifyListeners();
  }
}
