import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../application/settings_service.dart';

class SoundSensitivitySlider extends StatefulWidget {
  const SoundSensitivitySlider({super.key});

  @override
  State<SoundSensitivitySlider> createState() => _SoundSensitivitySliderState();
}

class _SoundSensitivitySliderState extends State<SoundSensitivitySlider> {
  @override
  Widget build(BuildContext context) {
    final settingsService = context.watch<SettingsService>();
    final settings = settingsService.settings;
    return ListTile(
      title: const Text('Sound Sensitivity Level'),
      subtitle: Slider(
        value: settings.soundSensitivityLevel,
        onChanged: (value) {
          settingsService.updateSoundSensitivity(value);
        },
        min: 0,
        max: 1,
      ),
    );
  }
}
