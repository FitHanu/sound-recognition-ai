import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '/l10n/generated/app_localizations.dart';
import '../application/settings_service.dart';

class SoundSensitivitySlider extends StatelessWidget {
  const SoundSensitivitySlider({super.key});

  @override
  Widget build(BuildContext context) {
    var l10n = AppLocalizations.of(context)!;
    return ListTile(
    title: Text(l10n.soundSensitivityLevel),
      subtitle: Consumer<SettingsService>(
        builder: (context, settingsService, child) => 
          Slider(
          value: settingsService.settings.soundSensitivityLevel,
          onChanged: (value) {
            settingsService.updateSoundSensitivity(value);
          },
          min: 0,
          max: 1,
      ),
        ) 
    );
  }
}
