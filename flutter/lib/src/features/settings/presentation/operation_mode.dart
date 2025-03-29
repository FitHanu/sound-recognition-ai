import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../../l10n/app_localizations.dart';
import '../application/settings_service.dart';

class OperationMode extends StatefulWidget {
  const OperationMode({super.key});

  @override
  State<OperationMode> createState() => _OperationModeState();
}

class _OperationModeState extends State<OperationMode> {
  final List<String> _modes = ['scheduled', 'continuously', 'Normal'];

  @override
  Widget build(BuildContext context) {
    final settingsService = context.watch<SettingsService>();
    final settings = settingsService.settings;
    final localizations = AppLocalizations.of(context)!;

    return ListTile(
      title: Text(localizations.operationMode),
      trailing: DropdownButton<String>(
        value: settings.operationMode,
        items: _modes.map((String mode) {
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
  }
}
