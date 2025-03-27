import 'package:flutter/material.dart';
import '../features/settings/presentation/sound_sensitivity_slider.dart';
import '../features/settings/presentation/recognised_sound_selection.dart';
import '../features/settings/presentation/alert_mode.dart';
import '../features/settings/presentation/operation_mode.dart';
import '../features/settings/presentation/battery_saver_mode.dart';
import '../features/settings/presentation/language_selection.dart';

class SettingsDrawer extends StatelessWidget {
  const SettingsDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
          children: [
            // Header
            const DrawerHeader(
              decoration: BoxDecoration(color: Colors.blue),
              child: Text(
                'ABA Settings',
                style: TextStyle(color: Colors.white, fontSize: 24),
              ),
            ),
            // Sub-components (modular)
            const SoundSensitivitySlider(),
            const RecognisedSoundSelection(),
            const AlertMode(),
            const OperationMode(),
            const BatterySaverMode(),
            const LanguageSelection(),
          ],
        ),
      );
  }
}
