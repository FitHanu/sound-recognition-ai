import 'package:flutter/foundation.dart';
import 'package:path/path.dart' as path;

import '../data/classification_config_repository.dart';
import '../domain/classification_config.dart';

class ClassificationConfigService extends ChangeNotifier {
  final ClassificationConfigRepository _repository =
      ClassificationConfigRepository();

  ClassificationConfig? _currentConfig;
  ClassificationConfig? get currentConfig => _currentConfig;

  List<String> _availableConfigs = [];
  List<String> get availableConfigs => _availableConfigs;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String? _error;
  String? get error => _error;

  ClassificationConfigService() {
    _initialize();
  }

  Future<void> _initialize() async {
    await loadDefaultConfig();
    await refreshAvailableConfigs();
  }

  // Load the default configuration
  Future<void> loadDefaultConfig() async {
    setLoading(true);
    try {
      await _repository.ensureDefaultConfigExists();
      final defaultPath = await _repository.defaultConfigPath;
      _currentConfig = await _repository.loadConfig(defaultPath);
      setError(null);
    } catch (e) {
      setError('Failed to load default configuration: $e');
    } finally {
      setLoading(false);
    }
  }

  // Refresh the list of available configurations
  Future<void> refreshAvailableConfigs() async {
    setLoading(true);
    try {
      _availableConfigs = await _repository.listConfigFiles();
      notifyListeners();
    } catch (e) {
      setError('Failed to list available configurations: $e');
    } finally {
      setLoading(false);
    }
  }

  // Load a specific configuration
  Future<void> loadConfig(String filePath) async {
    setLoading(true);
    try {
      _currentConfig = await _repository.loadConfig(filePath);
      setError(null);
    } catch (e) {
      setError('Failed to load configuration: $e');
    } finally {
      setLoading(false);
    }
  }

  // Save the current configuration
  Future<void> saveCurrentConfig({bool asNew = false}) async {
    if (_currentConfig == null) return;

    setLoading(true);
    try {
      final config = _currentConfig!;
      String filePath;

      if (asNew || config.isDefault) {
        // When saving as new or saving changes to default config
        final newConfig = config.copyWith(
          isDefault: false,
          name:
              asNew
                  ? 'custom_config_${DateTime.now().millisecondsSinceEpoch}'
                  : config.name,
        );
        filePath = await _repository.saveConfig(newConfig);
        _currentConfig = newConfig.copyWith(filePath: filePath);
      } else {
        // Just update the existing file
        filePath = await _repository.saveConfig(config, overwrite: true);
      }

      await refreshAvailableConfigs();
      setError(null);
    } catch (e) {
      setError('Failed to save configuration: $e');
    } finally {
      setLoading(false);
    }
  }

  // Update class enablement
  void updateClassEnabled(String className, bool enabled) {
    if (_currentConfig == null) return;

    final updatedClasses = Map<String, bool>.from(
      _currentConfig!.enabledClasses,
    );
    updatedClasses[className] = enabled;

    _currentConfig = _currentConfig!.copyWith(enabledClasses: updatedClasses);
    notifyListeners();
  }

  // Copy the current configuration to a new file
  Future<void> copyConfigToNewFile(String newName) async {
    if (_currentConfig == null) return;

    setLoading(true);
    try {
      final newConfig = _currentConfig!.copyWith(
        name: newName,
        isDefault: false,
      );

      final filePath = await _repository.saveConfig(newConfig);
      _currentConfig = newConfig.copyWith(filePath: filePath);

      await refreshAvailableConfigs();
      setError(null);
    } catch (e) {
      setError('Failed to copy configuration: $e');
    } finally {
      setLoading(false);
    }
  }

  // Restore the default configuration
  Future<void> restoreDefaultConfig() async {
    setLoading(true);
    try {
      _currentConfig = await _repository.restoreDefaultConfig();
      await refreshAvailableConfigs();
      setError(null);
    } catch (e) {
      setError('Failed to restore default configuration: $e');
    } finally {
      setLoading(false);
    }
  }

  // Delete a configuration
  Future<void> deleteConfig(String filePath) async {
    setLoading(true);
    try {
      final success = await _repository.deleteConfig(filePath);

      // If the current config was deleted, load the default
      if (success && _currentConfig?.filePath == filePath) {
        await loadDefaultConfig();
      }

      await refreshAvailableConfigs();
      setError(null);
    } catch (e) {
      setError('Failed to delete configuration: $e');
    } finally {
      setLoading(false);
    }
  }

  // Helper methods for loading state and errors
  void setLoading(bool loading) {
    _isLoading = loading;
    notifyListeners();
  }

  void setError(String? errorMessage) {
    _error = errorMessage;
    notifyListeners();
  }

  // Get the display name for a config file path
  String getConfigDisplayName(String filePath) {
    final name = path.basenameWithoutExtension(filePath);
    final isDefault =
        path.basename(filePath) ==
        ClassificationConfigRepository.defaultConfigFileName;
    return isDefault ? '$name (Default)' : name;
  }
}
