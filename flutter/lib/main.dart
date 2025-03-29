import 'package:danger_sound_recognition/src/routing/app_router.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';
import 'src/features/record/presentation/record_page.dart';
import 'src/features/settings/application/settings_service.dart';
import '/l10n/app_localizations.dart';


void main() async{
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize Settings
  final settingsService = SettingsService();
  await settingsService.loadSettings(); // Load from local storage or defaults
  runApp(
      ChangeNotifierProvider(
        create: (_) => settingsService,
        child: const MyApp(
        ),
      ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final settingsService = Provider.of<SettingsService>(context);

    return MaterialApp(
      locale: settingsService.locale,
      // locale: Locale('vi','VN'),
      supportedLocales: const [
        Locale('en', 'US'),
        Locale('vi', 'VN'),
      ],
      localizationsDelegates: [
        AppLocalizations.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ],
      // localizationsDelegates: AppLocalizations.localizationsDelegates,
      // supportedLocales: AppLocalizations.supportedLocales,
      localeResolutionCallback: (locale, supportedLocales) {
        for (var supportedLocale in supportedLocales) {
          if (supportedLocale.languageCode == locale?.languageCode &&
              supportedLocale.countryCode == locale?.countryCode) {
            return supportedLocale;
          }
        }
        return supportedLocales.first; // Default locale
      },
      onGenerateTitle: (context) => AppLocalizations.of(context)!.appBar,
      home: RecordPage(),
      onGenerateRoute: AppRouter.generateRoute,
    );
  }
}
