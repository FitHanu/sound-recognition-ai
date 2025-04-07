import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '/l10n/generated/app_localizations.dart';
import '../application/settings_service.dart';

class BatterySaverMode extends StatelessWidget {
  const BatterySaverMode({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return Consumer<SettingsService>(
      builder: (context, settingsService, child) {
      return SwitchListTile(
        title: Text(l10n.batterySaverMode),
        value: settingsService.settings.batterySaverMode,
        onChanged: (value) {
        settingsService.updateBatterySaverMode(value);
        },
        activeColor: Colors.green,
        inactiveThumbColor: Colors.red,
      );
      },
    );
  }
}
