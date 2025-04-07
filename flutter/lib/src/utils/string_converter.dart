import 'package:danger_sound_recognition/src/constants/model_constants.dart';

class StringUtils {
  static String mapToString(Map<String, bool> map) {
    return map.entries.map((entry) => '${entry.key}:${entry.value}').join('#');
  }
  static Map<String, bool> stringToMap(String input) {
      return Map.fromEntries(
        input.split('#').map((item) {
          var parts = item.split(':');
          return MapEntry(parts[0], parts[1] == 'true');
        }),
      );
  }

  /**
   * 
   */
  static String getModelConfigPath(String modelKey) {
    String p1 = ModelConstants.PROJECT_REL_MODEL_PATH;
    String p2 = ModelConstants.DEFAULT_CLASS_CONFIG_CSV;
    
    return '$p1$modelKey$p2';
  }
}