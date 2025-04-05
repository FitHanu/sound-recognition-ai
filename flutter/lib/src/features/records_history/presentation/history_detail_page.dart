import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '/l10n/generated/app_localizations.dart';
import '../application/history_service.dart';
import '../domain/history_record.dart';

class HistoryDetailPage extends StatelessWidget {
  final HistoryRecord record;

  const HistoryDetailPage({super.key, required this.record});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return Scaffold(
      appBar: AppBar(
        title: const Text('History Details'),
        actions: [
          IconButton(
            icon: const Icon(Icons.share),
            onPressed: () {
              Provider.of<HistoryService>(
                context,
                listen: false,
              ).shareRecord(record);
            },
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      record.formattedDate,
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 16),
                    _buildInfoRow(
                      context,
                      'Duration',
                      record.formattedDuration,
                      Icons.timer,
                    ),
                    const Divider(),
                    _buildInfoRow(
                      context,
                      'Filename',
                      record.filename,
                      Icons.insert_drive_file,
                    ),
                    const Divider(),
                    _buildInfoRow(
                      context,
                      'Detected Sounds',
                      record.detectedSoundsText,
                      Icons.hearing,
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),
            Text(
              'File Contents',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            Expanded(
              child: FutureBuilder<String>(
                future: _readFileContents(record),
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  }

                  if (snapshot.hasError) {
                    return Center(
                      child: Text(
                        'Error loading file: ${snapshot.error}',
                        style: const TextStyle(color: Colors.red),
                      ),
                    );
                  }

                  return Card(
                    child: SingleChildScrollView(
                      padding: const EdgeInsets.all(16),
                      child: Text(
                        snapshot.data ?? '',
                        style: const TextStyle(fontFamily: 'monospace'),
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(
    BuildContext context,
    String label,
    String value,
    IconData icon,
  ) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        children: [
          Icon(icon, color: Theme.of(context).primaryColor),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  label,
                  style: const TextStyle(fontSize: 14, color: Colors.grey),
                ),
                Text(
                  value,
                  style: const TextStyle(fontSize: 16),
                  overflow: TextOverflow.ellipsis,
                  maxLines: 2,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Future<String> _readFileContents(HistoryRecord record) async {
    try {
      final file = await Future.delayed(
        const Duration(milliseconds: 300),
        () => record.filePath,
      );
      return await Future.delayed(
        const Duration(milliseconds: 200),
        () => 'CSV file content would be shown here in a real implementation',
      );
    } catch (e) {
      return 'Error reading file: $e';
    }
  }
}
