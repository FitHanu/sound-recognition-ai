import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../../l10n/app_localizations.dart';
import '../application/settings_service.dart';

class AlertMode extends StatefulWidget {
  const AlertMode({super.key});

  @override
  State<AlertMode> createState() => _AlertModeState();
}

class _AlertModeState extends State<AlertMode> {
  @override
  Widget build(BuildContext context) {
    final settingsService = context.watch<SettingsService>();
    final settings = settingsService.settings;
    final localizations = AppLocalizations.of(context)!;
    return Column(
      children: [
         Padding(
          padding: EdgeInsets.all(16.0),
          child: Text(localizations.alertTitle,
              style: TextStyle(fontWeight: FontWeight.bold)),
        ),
        SwitchListTile(
          title: Text(localizations.vibration),
          value: settings.vibration,
          onChanged: (value) => settingsService.updateVibration(value),
        ),
        SwitchListTile(
          title: Text(localizations.alertSound),
          value: settings.alertSound,
          onChanged: (value) => settingsService.updateAlertSound(value),
        ),
      ],
    );
  }
}
