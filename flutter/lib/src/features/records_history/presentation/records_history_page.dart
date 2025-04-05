import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '/l10n/generated/app_localizations.dart';
import '../application/history_service.dart';
import 'history_list_item.dart';
import 'history_detail_page.dart';

class RecordsHistoryPage extends StatelessWidget {
  const RecordsHistoryPage({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return ChangeNotifierProvider(
      create: (_) => HistoryService(),
      child: Scaffold(
        appBar: AppBar(
          // Use simple string until localization is set up
          title: const Text('Records History'),
          actions: [
            Consumer<HistoryService>(
              builder:
                  (context, service, _) => IconButton(
                    icon: const Icon(Icons.refresh),
                    onPressed:
                        service.isLoading ? null : () => service.loadRecords(),
                  ),
            ),
          ],
        ),
        body: Consumer<HistoryService>(
          builder: (context, service, child) {
            if (service.isLoading) {
              return const Center(child: CircularProgressIndicator());
            }

            if (service.error != null) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      service.error!,
                      textAlign: TextAlign.center,
                      style: const TextStyle(color: Colors.red),
                    ),
                    const SizedBox(height: 16),
                    ElevatedButton(
                      onPressed: () => service.loadRecords(),
                      child: const Text('Retry'),
                    ),
                  ],
                ),
              );
            }

            if (service.records.isEmpty) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(Icons.history, size: 64, color: Colors.grey),
                    const SizedBox(height: 16),
                    const Text(
                      'No Records Found',
                      style: TextStyle(fontSize: 18),
                    ),
                    const SizedBox(height: 16),
                    ElevatedButton(
                      onPressed: () => service.loadRecords(),
                      child: const Text('Refresh'),
                    ),
                  ],
                ),
              );
            }

            return RefreshIndicator(
              onRefresh: () => service.loadRecords(),
              child: ListView.builder(
                itemCount: service.records.length,
                itemBuilder: (context, index) {
                  final record = service.records[index];
                  return HistoryListItem(
                    record: record,
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder:
                              (context) => ChangeNotifierProvider.value(
                                value: service,
                                child: HistoryDetailPage(record: record),
                              ),
                        ),
                      );
                    },
                    onDelete: () => service.deleteRecord(record),
                  );
                },
              ),
            );
          },
        ),
      ),
    );
  }
}
