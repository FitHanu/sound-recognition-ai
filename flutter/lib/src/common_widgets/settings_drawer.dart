import 'package:danger_sound_recognition/src/features/settings/presentation/battery_saver_mode.dart';
import 'package:danger_sound_recognition/src/features/settings/presentation/recognised_sound_selection.dart';
import 'package:flutter/material.dart';
import '../features/settings/presentation/sound_sensitivity_slider.dart';
import '../features/settings/presentation/alert_mode.dart';
import '../features/settings/presentation/operation_mode.dart';
import '../features/settings/presentation/language_selection.dart';
import '/l10n/generated/app_localizations.dart';

class SettingsDrawer extends StatelessWidget {
  const SettingsDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    var l10n = AppLocalizations.of(context)!;
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
          children: [
            // Header
            DrawerHeader(
              decoration: const BoxDecoration(color: Colors.blue),
              child: Text(
                l10n.appBar,
                // 'ABA Settings',
                style: const TextStyle(color: Colors.white, fontSize: 24),
              ),
            ),
            // Sub-components (modular)
            SoundSensitivitySlider(),
            RecognisedSoundSelection(),
            AlertMode(),
            OperationMode(),
            BatterySaverMode(),
            LanguageSelection(),
          ],
        ),
      );
  }
}
