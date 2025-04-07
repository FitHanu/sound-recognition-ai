import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '/l10n/generated/app_localizations.dart';
import '/src/config/locale.dart';

class LanguageSelection extends StatelessWidget {
  const LanguageSelection({super.key});
  @override
  Widget build(BuildContext context) {
    final languages = LanguageLocal();
    final l10n = AppLocalizations.of(context)!;
    final selectedLocale = Localizations.localeOf(context).toString();
    final List<Locale> locales  = AppLocalizations.supportedLocales;
    return ListTile(
      title: Text(l10n.language),
      trailing: Consumer<LocaleService> (
        builder: 
          (context, localeModel, child) => DropdownButton<String>(
            value: selectedLocale,
            items: locales.map((Locale locale) {
              var languageCode = locale.languageCode;
              var displayName = languages
              .getDisplayLanguage(languageCode)[LanguageLocal.NATIVE_NAME];
              return DropdownMenuItem<String>(
                value: languageCode,
                child: Text(displayName),
              );
            }).toList(),
            onChanged: (String? value) {
              if (value != null) {
                localeModel.set(Locale(value));
            }
          },
      ),
      )
    );
  }
}
