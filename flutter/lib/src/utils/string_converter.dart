class StringConverter {
  static String mapToString(Map<String, bool> map) {
    return map.entries.map((entry) => '${entry.key}:${entry.value}').join('#');
  }
  static Map<String, bool> stringToMap(String input) {
      return Map.fromEntries(
        input.split('#').map((item) {
          var parts = item.split(':');
          return MapEntry(parts[0], parts[1] == 'true');
        }),
      );
  }
}