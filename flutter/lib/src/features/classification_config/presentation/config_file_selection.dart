import 'package:flutter/material.dart';

class ConfigFileSelection extends StatelessWidget {
  final String selectedFilePath;
  final List<String> availableConfigs;
  final Function(String) onConfigSelected;
  final String Function(String) getConfigDisplayName;

  const ConfigFileSelection({
    super.key,
    required this.selectedFilePath,
    required this.availableConfigs,
    required this.onConfigSelected,
    required this.getConfigDisplayName,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Row(
        children: [
          const Text(
            'Configuration: ',
            style: TextStyle(fontWeight: FontWeight.bold),
          ),
          Expanded(
            child: DropdownButton<String>(
              value: selectedFilePath,
              isExpanded: true,
              hint: const Text('Select a configuration'),
              items:
                  availableConfigs.map((filePath) {
                    return DropdownMenuItem<String>(
                      value: filePath,
                      child: Text(getConfigDisplayName(filePath)),
                    );
                  }).toList(),
              onChanged: (value) {
                if (value != null) {
                  onConfigSelected(value);
                }
              },
            ),
          ),
        ],
      ),
    );
  }
}
