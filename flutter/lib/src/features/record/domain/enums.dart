enum Severity {
  LOW(0, 'LOW'),
  MEDIUM(1, 'MEDIUM'),
  HIGH(2, 'HIGH'),;

  final int ordinal;
  final String logicName;
  const Severity(this.ordinal, this.logicName);

  factory Severity.fromString(String severity) {
    switch (severity.toUpperCase()) {
      case 'LOW':
        return LOW;
      case 'MEDIUM':
        return MEDIUM;
      case 'HIGH':
        return HIGH;
      default:
        throw ArgumentError('Invalid severity level: $severity');
    }
  }
}


enum Action {
  LOG_HISTORY(0),
  ALERT(1),
  NOTIFY(2);
  // define more actions as needed

  final int ordinal;
  const Action(this.ordinal);
}