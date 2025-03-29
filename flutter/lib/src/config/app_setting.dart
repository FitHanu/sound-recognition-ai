class AppSettings {

  // Keys for local storage
  static const String alertSoundKey = 'alertSound';
  static const String languageKey = 'language';

  // Default values
  static const double defaultSoundSensitivityLevel = 0.5;
  static const Map<String, bool> defaultRecognisedSound = {
    'baby cry':false,
    'glass breaking':false,
    'gunshot':false,
    'scream':false,
    'siren':false
  };
  static const bool defaultVibration = true;
  static const bool defaultAlertSound = true;
  static const String defaultOperationMode = 'continuously';
  static const bool defaultBatterySaverMode = false;
  static const String defaultLanguage = 'en';
}
