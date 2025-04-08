import 'package:flutter/material.dart';
import '/l10n/generated/app_localizations.dart';
import '../../../common_widgets/settings_drawer.dart';
import '../../../routing/route_names.dart';

class RecordPage extends StatelessWidget {
  RecordPage({super.key});

  // GlobalKey to manage Scaffold
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();

  @override
  Widget build(BuildContext context) {
    final localizations = AppLocalizations.of(context)!;

    return Scaffold(
      key: _scaffoldKey, // Assigning the GlobalKey here
      appBar: AppBar(
        title: Text(localizations.appBar),
        leading: IconButton(
          icon: const Icon(Icons.menu), // Open Main Drawer
          onPressed: () {
            _scaffoldKey.currentState?.openDrawer(); // Open LEFT Drawer
          },
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings), // Open Settings Drawer
            onPressed: () {
              _scaffoldKey.currentState?.openEndDrawer(); // Open RIGHT Drawer
            },
          ),
        ],
      ),
      drawer: const MainDrawer(), // Main Navigation Drawer (left)
      endDrawer: const SettingsDrawer(), // Settings Drawer (right)
      body: Center(child: Text('Main Content Here')),
    );
  }
}

class MainDrawer extends StatelessWidget {
  const MainDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return Drawer(
      child: ListView(
        children: [
          DrawerHeader(
            decoration: const BoxDecoration(color: Colors.blue),
            child: Text(
              'Main Navigation',
              style: const TextStyle(color: Colors.white, fontSize: 24),
            ),
          ),
          ListTile(
            leading: const Icon(Icons.home),
            title: const Text('Home'),
            onTap: () {
              Navigator.pop(context);
              Navigator.pushReplacementNamed(context, RouteNames.record);
            },
          ),
          ListTile(
            leading: const Icon(Icons.history),
            title: const Text('Records History'), // Using hardcoded string
            onTap: () {
              Navigator.pop(context);
              Navigator.pushNamed(context, RouteNames.recordsHistory);
            },
          ),
        ],
      ),
    );
  }
}
