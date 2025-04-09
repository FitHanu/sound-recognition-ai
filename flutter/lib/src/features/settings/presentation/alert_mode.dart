import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '/l10n/generated/app_localizations.dart';
import '../application/settings_service.dart';

class AlertMode extends StatelessWidget {
  const AlertMode({super.key});

  @override
  Widget build(BuildContext context) {
    final settingsService = Provider.of<SettingsService>(context);
    final settings = settingsService.settings;
    final l10n = AppLocalizations.of(context)!;
    return Column(
      children: [
        Padding(
          padding: EdgeInsets.all(16.0),
          child: Text(l10n.alertTitle,
              style: TextStyle(fontWeight: FontWeight.bold)),
        ),
        SwitchListTile(
          title: Text(l10n.vibration),
          value: settings.vibration,
          onChanged: (value) => settingsService.updateVibration(value),
        ),
        SwitchListTile(
          title: Text(l10n.alertSound),
          value: settings.alertSound,
          onChanged: (value) => settingsService.updateAlertSound(value),
        ),
      ],
    );
  }
}
