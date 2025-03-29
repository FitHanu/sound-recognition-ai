import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '/l10n/generated/app_localizations.dart';
import '../application/settings_service.dart';

class OperationMode extends StatelessWidget {
  const OperationMode({super.key});
  @override
  Widget build(BuildContext context) {
    final List<String> modes = ['Scheduled', 'Continuously', 'Normal'];
    final l10n = AppLocalizations.of(context)!;

    return Consumer<SettingsService>(
      builder: 
        (context, settingsService, child) {
          String currentOperationMode = settingsService.settings.operationMode;
          if (!modes.contains(currentOperationMode)) {
            modes.add(currentOperationMode);
          }
          return ListTile(
            title: Text(l10n.operationMode),
            trailing: DropdownButton<String>(
              value: settingsService.settings.operationMode,
              items: modes.map((String mode) {
                return DropdownMenuItem<String>(
                value: mode,
                child: Text(mode),
                );
              }).toList(),
              onChanged: (value) {
                settingsService.updateOperationMode(value!);
              },
            ),
          );
      },
    );
  }
}
