import 'dart:developer';
import 'dart:typed_data';

import 'package:danger_sound_recognition/src/utils/csv_utils.dart';
import 'package:tflite_flutter/tflite_flutter.dart';

class AudioClassifierService {
  static const _modelPath = 'assets/models/yamnet/yamnet.tflite';
  static const _labelsPath = 'assets/models/yamnet/classes_default_config.csv';

  late Interpreter _interpreter;
  late final List<String> _labels;
  late Tensor _inputTensor;
  late Tensor _outputTensor;

  Future<void> _loadModel() async {
    final options = InterpreterOptions();
    // Load model from assets
    _interpreter = await Interpreter.fromAsset(_modelPath, options: options);

    _inputTensor = _interpreter.getInputTensors().first;
    log(_inputTensor.shape.toString());
    _outputTensor = _interpreter.getOutputTensors().first;
    log(_outputTensor.shape.toString());
    log('Interpreter loaded successfully');
  }

  // Load labels from assets
  Future<void> _loadLabels() async {
    int labelIndex = 1;
    List<List<dynamic>> classNames = await parseCsvFromAssets(_labelsPath);
    _labels = getCsvColumn(classNames, labelIndex)
                .map((e) => e.toString())
                .toList()
                .cast<String>();
  }

  Future<void> initHelper() async {
    await _loadLabels();
    await _loadModel();
  }

  Future<Map<String, double>> inference(Float32List input) async {
    // final output = [List<double>.filled(521, 0.0)];
    final output = {};
    _interpreter.run(input, output);
    var classification = <String, double>{};
    for (var i = 0; i < output[0].length; i++) {
      // Set label: points
      classification[_labels[i]] = output[0][i];
    }
    return classification;
  }

  closeInterpreter() {
    _interpreter.close();
  }
}