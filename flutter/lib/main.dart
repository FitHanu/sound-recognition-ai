import 'package:danger_sound_recognition/src/config/locale.dart';
import 'package:danger_sound_recognition/src/features/settings/application/settings_service.dart';
import 'package:danger_sound_recognition/src/routing/app_router.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'src/features/record/presentation/record_page.dart';
import '/l10n/generated/app_localizations.dart';


void main() async{
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize Settings
  final settingsService = SettingsService();
  await settingsService.loadSettings(); // Load from local storage or defaults
  runApp(
      MultiProvider(
        providers: [
          ChangeNotifierProvider(create: (context) => LocaleService()),
          ChangeNotifierProvider(create: (context) => settingsService),
        ],
        child: const MyApp(),
      ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final localeModel = Provider.of<LocaleService>(context);

    return MaterialApp(
        // supportedLocales: const [
        //   Locale('en', 'US'),
        //   Locale('vi', 'VN'),
        // ],
        // localizationsDelegates: [
        //   AppLocalizations.delegate,
        //   GlobalMaterialLocalizations.delegate,
        //   GlobalWidgetsLocalizations.delegate,
        // ],
        // localeResolutionCallback: (locale, supportedLocales) {
        //   for (var supportedLocale in supportedLocales) {
        //     if (supportedLocale.languageCode == locale?.languageCode &&
        //         supportedLocale.countryCode == locale?.countryCode) {
        //       return supportedLocale;
        //     }
        //   }
        //   return supportedLocales.first; // Default locale
        // },
        locale: localeModel.locale,
        localizationsDelegates: AppLocalizations.localizationsDelegates,
        supportedLocales: AppLocalizations.supportedLocales,
        onGenerateTitle: (context) => AppLocalizations.of(context)!.appBar,
        home: RecordPage(),
        onGenerateRoute: AppRouter.generateRoute,
    );
  }
}
