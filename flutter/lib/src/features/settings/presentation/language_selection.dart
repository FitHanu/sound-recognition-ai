import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../../l10n/app_localizations.dart';
import '../application/settings_service.dart';

class LanguageSelection extends StatefulWidget {
  const LanguageSelection({super.key});

  @override
  State<LanguageSelection> createState() => _LanguageSelectionState();
}

class _LanguageSelectionState extends State<LanguageSelection> {
  final List<String> _languages = ['en', 'vi'];

  @override
  Widget build(BuildContext context) {
    final settingsService = context.watch<SettingsService>();
    final settings = settingsService.settings;
    final localizations = AppLocalizations.of(context)!;
    return ListTile(
      title: Text(localizations.language),
      trailing: DropdownButton<String>(
        value: settings.language,
        items: _languages.map((String lang) {
          return DropdownMenuItem<String>(
            value: lang,
            child: Text(lang),
          );
        }).toList(),
        onChanged: (value) {
          settingsService.updateLanguage(value!);
        },
      ),
    );
  }
}
