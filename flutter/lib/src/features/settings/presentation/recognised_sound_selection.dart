import 'package:danger_sound_recognition/src/config/app_setting.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '/l10n/generated/app_localizations.dart';
import '../application/settings_service.dart';

class RecognisedSoundSelection extends StatelessWidget {
  const RecognisedSoundSelection({super.key});

  @override
  Widget build(BuildContext context) {
    
    final Map<String, bool> options =
    Map<String, bool>.from( AppSettings.defaultRecognisedSound );

    // Map to dynamically access AppLocalizations fields
    Map<String, String> getLocalizedMap(AppLocalizations localizations) => {
      'scream': localizations.scream,
      'glass breaking': localizations.glassBreaking,
      'siren': localizations.siren,
      'gunshot': localizations.gunShot,
      'baby cry': localizations.babyCry,
    };

    final l10n = AppLocalizations.of(context)!;
    final localizedMap = getLocalizedMap(l10n);

    return Consumer<SettingsService>(
      builder: (context, settingsService, child) {
      final settings = settingsService.settings;
      return Column(
        children: [
        Padding(
          padding: EdgeInsets.all(16.0),
          child: Text(l10n.soundFormTitle,
            style: TextStyle(fontWeight: FontWeight.bold)),
        ),
        GridView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 4,
          ),
          itemCount: settings.recognisedSound.length,
          itemBuilder: (context, index) {
          final key = (settings.recognisedSound.keys.elementAt(index));
          return CheckboxListTile(
            title: Text(localizedMap[key] ?? key),
            // English value should be kept instead of Vietnamese
            value: settings.recognisedSound[key],
            onChanged: (bool? value) {
            options[key] = value ?? false;
            options.forEach(
              (key, value) => debugPrint("$key:$value"));
            settingsService.updateRecognisedSound(options);
            },
            controlAffinity: ListTileControlAffinity.leading,
          );
          },
        ),
        ],
      );
      },
    );
  }
}
