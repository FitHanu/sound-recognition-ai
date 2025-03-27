import 'package:flutter/material.dart';
import '../../../common_widgets/settings_drawer.dart';

class RecordPage extends StatelessWidget {
  RecordPage({super.key});

  // GlobalKey to manage Scaffold
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _scaffoldKey, // Assigning the GlobalKey here
      appBar: AppBar(
        title: const Text('ABA Systems'),
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
      body: const Center(child: Text('Main Content Here')),
    );
  }
}

class MainDrawer extends StatelessWidget {
  const MainDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: ListView(
        children: const [
          DrawerHeader(
            decoration: BoxDecoration(color: Colors.blue),
            child: Text('Main Navigation'),
          ),
          ListTile(title: Text('Home')),
        ],
      ),
    );
  }
}
