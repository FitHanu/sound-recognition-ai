import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

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
    return SwitchListTile(
      title: const Text('Battery Saver Mode'),
      value: settings.batterySaverMode,
      onChanged: (value) {
        settingsService.updateBatterySaverMode(value);
      },
      activeColor: Colors.green,
      inactiveThumbColor: Colors.red,
    );
  }
}
