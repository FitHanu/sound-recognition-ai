import 'package:danger_sound_recognition/src/config/app_setting.dart';
import 'package:danger_sound_recognition/src/utils/string_converter.dart';
import 'package:flutter/cupertino.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../domain/settings.dart';

class SettingsRepository {

  Settings? factorySetting;
  
  Future<Settings> loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    bool flag = prefs.containsKey('recognisedSound');
    String recognisedSounds = prefs.getString('recognisedSound')??'';
    Settings setting = Settings(
      soundSensitivityLevel: prefs.getDouble('soundSensitivityLevel') ?? AppSettings.defaultSoundSensitivityLevel,
      recognisedSound: flag
          ? StringUtils.stringToMap(recognisedSounds)
          : AppSettings.defaultRecognisedSound,
      vibration: prefs.getBool('vibration') ?? AppSettings.defaultVibration,
      alertSound: prefs.getBool('alertSound') ?? AppSettings.defaultAlertSound,
      operationMode: prefs.getString('operationMode') ?? AppSettings.defaultOperationMode,
      batterySaverMode: prefs.getBool('batterySaverMode') ?? AppSettings.defaultBatterySaverMode,
      language: prefs.getString('language') ?? AppSettings.defaultLanguageCode,
    );
    debugPrint("Load the latest setting repository");
    setting.print();
    return setting;
  }
  /// Save only sound sensitivity level
  Future<bool> saveSoundSensitivityLevel(double level) async {
    final prefs = await SharedPreferences.getInstance();
    factorySetting?.soundSensitivityLevel = level;
    debugPrint('Alert sound sensitivity level to $level');
    return await prefs.setDouble('soundSensitivityLevel', level);
  }

  /// Save RecognisedSound
  Future<bool> saveRecognisedSound(Map<String, bool> sounds) async {
    final prefs = await SharedPreferences.getInstance();
    factorySetting?.recognisedSound = sounds;
    String handledSound = StringUtils.mapToString(sounds);
    debugPrint('Alert RecognisedSound to $handledSound');
    return await prefs.setString('recognisedSound', handledSound);
  }

  /// Save vibration
  Future<bool> saveVibration(bool vibration) async {
    final prefs = await SharedPreferences.getInstance();
    factorySetting?.vibration = vibration;
    debugPrint('Alert sound changed from to $vibration');
    return await prefs.setBool('vibration', vibration);
  }

  /// Save alert sound
  Future<bool> saveAlertSound(bool alertSound) async {
    final prefs = await SharedPreferences.getInstance();
    factorySetting?.alertSound = alertSound;
    debugPrint('Alert alertSound to $alertSound');
    return await prefs.setBool('alertSound', alertSound);
  }

  /// Save operation mode
  Future<bool> saveOperationMode(String mode) async {
    final prefs = await SharedPreferences.getInstance();
    factorySetting?.operationMode = mode;
    debugPrint('Alert OperationMode to $mode');
    return await prefs.setString('operationMode', mode);
  }

  /// Save battery saver mode
  Future<bool> saveBatterySaverMode(bool mode) async {
    final prefs = await SharedPreferences.getInstance();
    factorySetting?.batterySaverMode = mode;
    debugPrint('Alert Battery to $mode');
    return await prefs.setBool('batterySaverMode', mode);
  }

  /// Save language
  Future<bool> saveLanguage(String language) async {
    final prefs = await SharedPreferences.getInstance();
    factorySetting?.language = language;
    debugPrint('Alert language to $language');
    return await prefs.setString('language', language);
  }
}
