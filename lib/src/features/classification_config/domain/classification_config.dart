import 'package:flutter/foundation.dart';

class ClassificationConfig {
  final String name;
  final Map<String, bool> enabledClasses;
  final bool isDefault;
  final String filePath;

  ClassificationConfig({
    required this.name,
    required this.enabledClasses,
    this.isDefault = false,
    required this.filePath,
  });

  ClassificationConfig copyWith({
    String? name,
    Map<String, bool>? enabledClasses,
    bool? isDefault,
    String? filePath,
  }) {
    return ClassificationConfig(
      name: name ?? this.name,
      enabledClasses: enabledClasses ?? Map.from(this.enabledClasses),
      isDefault: isDefault ?? this.isDefault,
      filePath: filePath ?? this.filePath,
    );
  }

  void print() {
    debugPrint("Config name: $name");
    debugPrint("Is default: $isDefault");
    debugPrint("File path: $filePath");
    enabledClasses.forEach((key, value) => debugPrint("$key: $value"));
  }
}
