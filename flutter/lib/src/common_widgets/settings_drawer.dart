import 'package:flutter/material.dart';
import '../features/settings/presentation/sound_sensitivity_slider.dart';
import '../features/settings/presentation/recognised_sound_selection.dart';
import '../features/settings/presentation/alert_mode.dart';
import '../features/settings/presentation/operation_mode.dart';
import '../features/settings/presentation/battery_saver_mode.dart';
import '../features/settings/presentation/language_selection.dart';
import '../../l10n/app_localizations.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

class SettingsDrawer extends StatelessWidget {
  const SettingsDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
          children: [
            // Header
             DrawerHeader(
              decoration: const BoxDecoration(color: Colors.blue),
              child: Text(
                AppLocalizations.of(context)!.appBar,
                // 'ABA Settings',
                style: const TextStyle(color: Colors.white, fontSize: 24),
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
