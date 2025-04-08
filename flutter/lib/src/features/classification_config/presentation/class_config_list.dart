import 'package:flutter/material.dart';

class ClassConfigList extends StatelessWidget {
  final Map<String, bool> classes;
  final Function(String, bool) onToggle;

  const ClassConfigList({
    super.key,
    required this.classes,
    required this.onToggle,
  });

  @override
  Widget build(BuildContext context) {
    final classNames = classes.keys.toList();

    return ListView.builder(
      itemCount: classNames.length,
      itemBuilder: (context, index) {
        final className = classNames[index];
        final isEnabled = classes[className] ?? false;

        return ListTile(
          title: Text(className),
          subtitle: Text(isEnabled ? 'Warnings enabled' : 'Warnings disabled'),
          trailing: Switch(
            value: isEnabled,
            onChanged: (value) => onToggle(className, value),
            activeColor: Colors.green,
            inactiveThumbColor: Colors.red,
          ),
          leading: Icon(
            isEnabled ? Icons.notifications_active : Icons.notifications_off,
            color: isEnabled ? Colors.green : Colors.red,
          ),
        );
      },
    );
  }
}
