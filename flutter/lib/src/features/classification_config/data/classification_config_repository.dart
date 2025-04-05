import 'dart:io';
import 'package:csv/csv.dart';
import 'package:flutter/foundation.dart';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart' as path;

import '../domain/classification_config.dart';

class ClassificationConfigRepository {
  // Default config file name
  static const String defaultConfigFileName = 'default_config.csv';

  // Get the path to the app's documents directory
  Future<String> get _localPath async {
    final directory = await getApplicationDocumentsDirectory();
    final configDir = Directory('${directory.path}/configs');
    if (!await configDir.exists()) {
      await configDir.create(recursive: true);
    }
    return configDir.path;
  }

  // Get the path to the default config file
  Future<String> get defaultConfigPath async {
    final localPath = await _localPath;
    return path.join(localPath, defaultConfigFileName);
  }

  // Create default config file if it doesn't exist
  Future<void> ensureDefaultConfigExists() async {
    final defaultPath = await defaultConfigPath;
    final defaultFile = File(defaultPath);

    if (!await defaultFile.exists()) {
      // Create a default configuration with common sounds
      final defaultConfig = [
        ['class_name', 'enabled'],
        ['baby cry', 'true'],
        ['glass breaking', 'true'],
        ['gunshot', 'true'],
        ['scream', 'true'],
        ['siren', 'true'],
      ];

      final csvData = const ListToCsvConverter().convert(defaultConfig);
      await defaultFile.writeAsString(csvData);
    }
  }

  // Load a configuration from a file
  Future<ClassificationConfig> loadConfig(String filePath) async {
    try {
      final file = File(filePath);

      if (!await file.exists()) {
        throw FileSystemException('Configuration file not found', filePath);
      }

      final csvString = await file.readAsString();
      final csvTable = const CsvToListConverter().convert(csvString);

      final enabledClasses = <String, bool>{};

      // Skip header row and process data
      for (int i = 1; i < csvTable.length; i++) {
        final row = csvTable[i];
        if (row.length >= 2) {
          final className = row[0].toString();
          final isEnabled = row[1].toString().toLowerCase() == 'true';
          enabledClasses[className] = isEnabled;
        }
      }

      final fileName = path.basename(filePath);
      final isDefault = fileName == defaultConfigFileName;

      return ClassificationConfig(
        name: path.basenameWithoutExtension(filePath),
        enabledClasses: enabledClasses,
        isDefault: isDefault,
        filePath: filePath,
      );
    } catch (e) {
      debugPrint('Error loading configuration: $e');
      rethrow;
    }
  }

  // Save a configuration to a file
  Future<String> saveConfig(
    ClassificationConfig config, {
    bool overwrite = false,
  }) async {
    try {
      String filePath = config.filePath;

      // If this is not the default config and we're creating a new file
      if (!config.isDefault && !overwrite) {
        final localPath = await _localPath;
        filePath = path.join(localPath, '${config.name}.csv');

        // Ensure the filename is unique
        final file = File(filePath);
        int counter = 1;
        while (await file.exists()) {
          filePath = path.join(localPath, '${config.name}_$counter.csv');
          counter++;
        }
      }

      // Convert to CSV format
      final csvData = [
        ['class_name', 'enabled'],
        ...config.enabledClasses.entries.map(
          (entry) => [entry.key, entry.value.toString()],
        ),
      ];

      final csvString = const ListToCsvConverter().convert(csvData);

      // Write to file
      final file = File(filePath);
      await file.writeAsString(csvString);

      return filePath;
    } catch (e) {
      debugPrint('Error saving configuration: $e');
      rethrow;
    }
  }

  // List all available configuration files
  Future<List<String>> listConfigFiles() async {
    try {
      final localPath = await _localPath;
      final directory = Directory(localPath);

      final files =
          await directory
              .list()
              .where(
                (entity) =>
                    entity is File && path.extension(entity.path) == '.csv',
              )
              .map((entity) => entity.path)
              .toList();

      return files;
    } catch (e) {
      debugPrint('Error listing configuration files: $e');
      return [];
    }
  }

  // Delete a configuration file
  Future<bool> deleteConfig(String filePath) async {
    try {
      // Don't allow deleting the default config
      if (path.basename(filePath) == defaultConfigFileName) {
        return false;
      }

      final file = File(filePath);
      if (await file.exists()) {
        await file.delete();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error deleting configuration file: $e');
      return false;
    }
  }

  // Restore default configuration
  Future<ClassificationConfig> restoreDefaultConfig() async {
    await ensureDefaultConfigExists();
    final defaultPath = await defaultConfigPath;
    return await loadConfig(defaultPath);
  }
}
