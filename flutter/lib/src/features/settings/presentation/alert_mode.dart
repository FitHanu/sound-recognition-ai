import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

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
    
    return Column(
      children: [
        const Padding(
          padding: EdgeInsets.all(16.0),
          child: Text('Alert Notification',
              style: TextStyle(fontWeight: FontWeight.bold)),
        ),
        SwitchListTile(
          title: const Text('Vibration'),
          value: settings.vibration,
          onChanged: (value) => settingsService.updateVibration(value),
        ),
        SwitchListTile(
          title: const Text('Alert Sound'),
          value: settings.alertSound,
          onChanged: (value) => settingsService.updateAlertSound(value),
        ),
      ],
    );
  }
}
