import 'package:danger_sound_recognition/src/features/settings/presentation/battery_saver_mode.dart';
import 'package:flutter/cupertino.dart';

class Settings {
  // #sound sensitivity level: 50
  // #recognised sound: scream, gunshot, siren
  // #alert mode: vibration, sound
  // #operation mode: normal
  // #battery save mode: true
  // #language: english
  double soundSensitivityLevel = 0.0;
  Map<String, bool> recognisedSound;
  bool vibration = false;
  bool alertSound = false;
  String operationMode = '';
  bool batterySaverMode = false;
  String language = '';

  Settings({
    required this.soundSensitivityLevel,
    required this.recognisedSound,
    required this.vibration,
    required this.alertSound,
    required this.operationMode,
    required this.batterySaverMode,
    required this.language,
  });

  void print (){
    debugPrint("$soundSensitivityLevel");
    debugPrint("$language");
    debugPrint("$vibration");
    debugPrint("$alertSound");
    debugPrint("$operationMode");
    debugPrint("$batterySaverMode");
    recognisedSound.forEach(
        (key,value) => debugPrint("$key:$value")
    );
}

}
