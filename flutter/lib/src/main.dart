import 'package:danger_sound_recognition/src/routing/app_router.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'features/record/presentation/record_page.dart';
import 'features/settings/application/settings_service.dart';

void main() async{
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize Settings
  final settingsService = SettingsService();
  await settingsService.loadSettings(); // Load from local storage or defaults
  runApp(
      ChangeNotifierProvider(
        create: (_) => settingsService,
        child: const MyApp(),
      ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: RecordPage(),
      onGenerateRoute: AppRouter.generateRoute,
    );
  }
}
