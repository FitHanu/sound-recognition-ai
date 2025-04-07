import 'package:danger_sound_recognition/src/features/record/domain/recorder_config.dart';
import 'package:flutter/material.dart';
import 'package:tflite_flutter/tflite_flutter.dart';


/**
 * Service:
 * + Manages:
 *  - RecorderConfig
 *    - provides: modelPath, classifierSettings, ...  
 */
class ClassifierService extends ChangeNotifier{

  ClassifierService._internal() {
    
  }

  late RecorderConfig _recorderConfig;
  late Interpreter _interpreter;

}