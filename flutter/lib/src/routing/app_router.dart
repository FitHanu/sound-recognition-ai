import 'package:flutter/material.dart';
import '../features/record/presentation/record_page.dart';
import '../features/records_history/presentation/records_history_page.dart';
// import '../features/settings/presentation/settings_page.dart';
import 'route_names.dart';

class AppRouter {
  static Route<dynamic> generateRoute(RouteSettings settings) {
    switch (settings.name) {
      case RouteNames.record:
        return MaterialPageRoute(builder: (_) => RecordPage());
      case RouteNames.recordsHistory:
        return MaterialPageRoute(builder: (_) => const RecordsHistoryPage());
      // case RouteNames.settings:
      //   return MaterialPageRoute(builder: (_) => const SettingsPage());
      default:
        return MaterialPageRoute(
          builder:
              (_) =>
                  const Scaffold(body: Center(child: Text('Page Not Found'))),
        );
    }
  }
}
