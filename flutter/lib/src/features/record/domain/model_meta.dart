class ClassificationModelMeta {
  final String key;
  final String name;
  final String type;
  final String format;
  final String version;
  final String path;

  ClassificationModelMeta({
    required this.key,
    required this.name,
    required this.type,
    required this.format,
    required this.version,
    required this.path,
  });
  
  factory ClassificationModelMeta.fromJson(String key, Map<String, dynamic> data) {
    return ClassificationModelMeta(
      key: key,
      name: data['name'] as String,
      type: data['type'] as String,
      format: data['format'] as String,
      version: data['version'] as String,
      path: data['path'] as String,
    );
  }

  @override
  String toString() {
    return 'ClassificationModelMeta(name: $name, type: $type, format: $format, version: $version, path: $path)';
  }
}
