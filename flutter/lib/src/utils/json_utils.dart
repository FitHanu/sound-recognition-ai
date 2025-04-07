import 'dart:convert';
import 'package:danger_sound_recognition/src/features/record/domain/model_meta.dart';
import 'package:flutter/services.dart' show rootBundle;



class JsonUtils {
  static Future<Map<String, dynamic>> readJsonFromAssets(String assetPath) async {
    try {
      final String jsonString = await rootBundle.loadString(assetPath);
      return json.decode(jsonString) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Error reading JSON from $assetPath: $e');
    }
  }


  static Future<ClassificationModelMeta> getModelMeta(String assetPath) async {
    try {
      final Map<String, dynamic> jsonData = await readJsonFromAssets(assetPath);
      return ClassificationModelMeta.fromJson(jsonData);
    } catch (e) {
      throw Exception('Error reading ClassificationModelMeta from $assetPath: $e');
    }
  }
}
