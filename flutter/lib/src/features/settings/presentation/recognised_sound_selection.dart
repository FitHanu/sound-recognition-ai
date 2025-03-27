import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../application/settings_service.dart';

class RecognisedSoundSelection extends StatefulWidget {
  const RecognisedSoundSelection({super.key});

  @override
  State<RecognisedSoundSelection> createState() => _RecognisedSoundSelectionState();
}

class _RecognisedSoundSelectionState extends State<RecognisedSoundSelection> {
  final Map<String, bool> _options = {
    'Scream': false,
    'Glass Breaking': false,
    'Gunshot': false,
    'Siren': false,
    'Baby Cry': false,
  };

  @override
  Widget build(BuildContext context) {
    final settingsService = context.watch<SettingsService>();
    final settings = settingsService.settings;
    return Column(
      children: [
        const Padding(
          padding: EdgeInsets.all(16.0),
          child: Text('Select the sound type to recognize',
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
            final key = settings.recognisedSound.keys.elementAt(index);
            return CheckboxListTile(
              title: Text(key),
              value: settings.recognisedSound[key],
              onChanged: (bool? value) {
                _options[key] = value ?? false;
                settingsService.updateRecognisedSound(_options);
              },
              controlAffinity: ListTileControlAffinity.leading,
            );
          },
        ),
      ],
    );
  }
}
