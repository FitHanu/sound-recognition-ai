import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:path/path.dart' as path;
import '/l10n/generated/app_localizations.dart';

import '../application/classification_config_service.dart';
import 'class_config_list.dart';
import 'config_file_selection.dart';

class ClassificationConfigPage extends StatefulWidget {
  const ClassificationConfigPage({Key? key}) : super(key: key);

  @override
  State<ClassificationConfigPage> createState() =>
      _ClassificationConfigPageState();
}

class _ClassificationConfigPageState extends State<ClassificationConfigPage> {
  @override
  void initState() {
    super.initState();
    print('ClassificationConfigPage initialized'); // Debug print
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => ClassificationConfigService(),
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Classification Settings'),
          actions: [
            Consumer<ClassificationConfigService>(
              builder:
                  (context, service, _) => IconButton(
                    icon: const Icon(Icons.refresh),
                    onPressed:
                        service.isLoading
                            ? null
                            : service.refreshAvailableConfigs,
                    tooltip: 'Refresh configurations',
                  ),
            ),
          ],
        ),
        body: Consumer<ClassificationConfigService>(
          builder: (context, service, _) {
            if (service.isLoading) {
              return const Center(child: CircularProgressIndicator());
            }

            if (service.error != null) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      service.error!,
                      textAlign: TextAlign.center,
                      style: const TextStyle(color: Colors.red),
                    ),
                    const SizedBox(height: 16),
                    ElevatedButton(
                      onPressed: service.refreshAvailableConfigs,
                      child: const Text('Retry'),
                    ),
                  ],
                ),
              );
            }

            if (service.currentConfig == null) {
              return const Center(child: Text('No configuration loaded'));
            }

            return Column(
              children: [
                // Config file selection
                ConfigFileSelection(
                  selectedFilePath: service.currentConfig!.filePath,
                  availableConfigs: service.availableConfigs,
                  onConfigSelected: service.loadConfig,
                  getConfigDisplayName: service.getConfigDisplayName,
                ),

                // Current config info
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Row(
                    children: [
                      Expanded(
                        child: Text(
                          'Current config: ${service.getConfigDisplayName(service.currentConfig!.filePath)}',
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                      ),
                      IconButton(
                        icon: const Icon(Icons.save),
                        onPressed: () => service.saveCurrentConfig(),
                        tooltip: 'Save changes',
                      ),
                      IconButton(
                        icon: const Icon(Icons.file_copy),
                        onPressed: () => _showCopyDialog(context, service),
                        tooltip: 'Copy to new file',
                      ),
                    ],
                  ),
                ),

                const Divider(),

                // Class configuration list
                Expanded(
                  child: ClassConfigList(
                    classes: service.currentConfig!.enabledClasses,
                    onToggle: service.updateClassEnabled,
                  ),
                ),

                // Action buttons at the bottom
                Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      ElevatedButton.icon(
                        icon: const Icon(Icons.restore),
                        label: const Text('Restore Default'),
                        onPressed: service.restoreDefaultConfig,
                      ),
                      if (!service.currentConfig!.isDefault)
                        ElevatedButton.icon(
                          icon: const Icon(Icons.delete),
                          label: const Text('Delete Config'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.red,
                            foregroundColor: Colors.white,
                          ),
                          onPressed:
                              () => _confirmDelete(
                                context,
                                service,
                                service.currentConfig!.filePath,
                              ),
                        ),
                    ],
                  ),
                ),
              ],
            );
          },
        ),
      ),
    );
  }

  // Show dialog to confirm deleting a configuration
  void _confirmDelete(
    BuildContext context,
    ClassificationConfigService service,
    String filePath,
  ) {
    showDialog(
      context: context,
      builder:
          (context) => AlertDialog(
            title: const Text('Delete Configuration?'),
            content: Text(
              'Are you sure you want to delete "${service.getConfigDisplayName(filePath)}"? This cannot be undone.',
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('Cancel'),
              ),
              TextButton(
                onPressed: () {
                  service.deleteConfig(filePath);
                  Navigator.pop(context);
                },
                child: const Text(
                  'Delete',
                  style: TextStyle(color: Colors.red),
                ),
              ),
            ],
          ),
    );
  }

  // Show dialog to copy configuration to a new file
  void _showCopyDialog(
    BuildContext context,
    ClassificationConfigService service,
  ) {
    final nameController = TextEditingController(
      text:
          'Copy of ${path.basenameWithoutExtension(service.currentConfig!.filePath)}',
    );

    showDialog(
      context: context,
      builder:
          (context) => AlertDialog(
            title: const Text('Copy Configuration'),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text('Enter a name for the new configuration:'),
                TextField(
                  controller: nameController,
                  decoration: const InputDecoration(
                    labelText: 'Configuration Name',
                  ),
                ),
              ],
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('Cancel'),
              ),
              TextButton(
                onPressed: () {
                  if (nameController.text.trim().isNotEmpty) {
                    service.copyConfigToNewFile(nameController.text.trim());
                    Navigator.pop(context);
                  }
                },
                child: const Text('Save'),
              ),
            ],
          ),
    );
  }
}
