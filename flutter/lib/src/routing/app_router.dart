import 'package:danger_sound_recognition/src/features/debug_and_testing/representation/dat.dart';
import 'package:flutter/material.dart';
import '../features/record/presentation/record_page.dart';
import 'route_names.dart';

class AppRouter {
  static Route<dynamic> generateRoute(RouteSettings settings) {
    switch (settings.name) {
      case RouteNames.RECORD:
        return MaterialPageRoute(builder: (_) => RecordPage());
      case RouteNames.DEBUG_TEST:
        return MaterialPageRoute(builder: (_) => DebuggingAndTesting());
      // case RouteNames.settings:
      //   return MaterialPageRoute(builder: (_) => const SettingsPage());
      default:
        return MaterialPageRoute(
          builder: (_) => const Scaffold(
            body: Center(child: Text('Page Not Found')),
          ),
        );
    }
  }
}
