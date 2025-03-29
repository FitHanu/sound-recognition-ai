import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../../l10n/app_localizations.dart';
import '../application/settings_service.dart';

class BatterySaverMode extends StatefulWidget {
  const BatterySaverMode({super.key});

  @override
  State<BatterySaverMode> createState() => _BatterySaverModeState();
}

class _BatterySaverModeState extends State<BatterySaverMode> {

  @override
  Widget build(BuildContext context) {
    final settingsService = context.watch<SettingsService>();
    final settings = settingsService.settings;
    final localizations = AppLocalizations.of(context)!;

    return SwitchListTile(
      title: Text(localizations.batterySaverMode),
      value: settings.batterySaverMode,
      onChanged: (value) {
        settingsService.updateBatterySaverMode(value);
      },
      activeColor: Colors.green,
      inactiveThumbColor: Colors.red,
    );
  }
}
