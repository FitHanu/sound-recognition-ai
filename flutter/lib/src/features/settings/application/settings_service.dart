import 'package:flutter/material.dart';
import '../data/settings_repository.dart';
import '../domain/settings.dart';

class SettingsService extends ChangeNotifier {
  final SettingsRepository _repository = SettingsRepository();
  late Settings _settings;
  Settings get settings => _settings;
  // late Locale _locale;
  // Locale get locale => _locale;

  SettingsService() {
    loadSettings();
  }

  Future<void> loadSettings() async {
    _settings = await _repository.loadSettings();
    //set Locale object after loading
    // final code = (_settings.language=='vi')?'VN':'US';
    // _locale = Locale(_settings.language, code);
    notifyListeners();
  }

  /// Update sound sensitivity level
  Future<void> updateSoundSensitivity(double level) async {
    _settings.soundSensitivityLevel = level;
    await _repository.saveSoundSensitivityLevel(level);
    notifyListeners();
  }

  /// Update vibration setting
  Future<void> updateVibration(bool value) async {
    _settings.vibration = value;
    await _repository.saveVibration(value);
    notifyListeners();
  }

  /// Update alert sound
  Future<void> updateAlertSound(bool value) async {
    _settings.alertSound = value;
    await _repository.saveAlertSound(value);
    notifyListeners(); // Notify UI of the change
  }

  /// Update RecognisedSound
  Future<void> updateRecognisedSound(Map<String, bool> sounds) async {
    _settings.recognisedSound = sounds;
    await _repository.saveRecognisedSound(sounds);
    notifyListeners();
  }

  /// Update operation mode
  Future<void> updateOperationMode(String mode) async {
    _settings.operationMode = mode;
    await _repository.saveOperationMode(mode);
    notifyListeners();
  }

  /// Update battery saver mode
  Future<void> updateBatterySaverMode(bool value) async {
    _settings.batterySaverMode = value;
    await _repository.saveBatterySaverMode(value);
    notifyListeners();
  }

  // /// Update language setting
  // Future<void> updateLanguage(String languageCode) async {
  //   _settings.language = languageCode;
  //   final language = (languageCode=='vi')?'VN':'US';
  //   _locale = Locale(languageCode, language);
  //   await _repository.saveLanguage(languageCode);
  //   notifyListeners();
  // }

}
