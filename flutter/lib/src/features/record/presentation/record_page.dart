import 'package:flutter/material.dart';
import '../../../common_widgets/app_drawer.dart';

class RecordPage extends StatelessWidget {
  const RecordPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('ABA SYSTEMS')),
      drawer: const AppDrawer(),
      body: const Center(child: Text('Record Page')),
    );
  }
}
