import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:tflite_flutter/tflite_flutter.dart';

Future<Interpreter?> loadYamnetModel() async {
  final String modelPath = 'assets/yamnet.tflite';
  try {
    await rootBundle.load(modelPath);
    Interpreter interpreter = await Interpreter.fromAsset(modelPath);
    print('✅ Yamnet.tflite is loaded successfully!');
    return interpreter;
  } catch (e) {
    print('❌ Cannot load yamnet.tflite: $e');
    return null;
  }
}

