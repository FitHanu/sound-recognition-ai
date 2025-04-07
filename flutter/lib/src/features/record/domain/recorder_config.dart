import 'package:danger_sound_recognition/src/constants/model_constants.dart' show ModelConstants;
import 'package:danger_sound_recognition/src/features/record/domain/model_meta.dart';
import 'package:danger_sound_recognition/src/features/record/domain/enums.dart';
import 'package:danger_sound_recognition/src/utils/csv_utils.dart';
import 'package:danger_sound_recognition/src/utils/string_converter.dart';

class RecorderConfig {
  int sampleRate = 16000;
  int nChannels = 1;
  int format = 2; // PCM_16
  ClassificationModelMeta modelMeta = ModelConstants.DEFAULT_MODEL as ClassificationModelMeta;
  AlertConfig alertConfig;

  RecorderConfig({
    required String modelKey,
    required List<List<dynamic>> csvData,
  })  : modelMeta = ModelConstants.DEFAULT_MODEL as ClassificationModelMeta,
        alertConfig = AlertConfig.fromCsv(csvData);


  RecorderConfig.default() {
    var modelMeta = ModelConstants.DEFAULT_MODEL as ClassificationModelMeta;
    String metaCsvPath = StringUtils.getModelConfigPath(modelMeta.name);
  }


  
}

class AlertConfig {
  /* number of classnames */
  final int classCount;

  /* alert config */
  final Map<int, LabelConfig> alertConfig;

  AlertConfig(this.classCount, this.alertConfig);

  factory AlertConfig.fromCsv(List<List<dynamic>> csvData) {
    if (CsvUtils.isEmtpyCsv(csvData)) {
      throw Exception('CSV data is empty or invalid.');
    }
    var shape = CsvUtils.getShape(csvData);
    int classCount = shape[0] - 1;
    Map<int, LabelConfig> alertConfig = {};

    for (int i = 0; i < classCount; i++) {
      int rowIndex = i + 1; // Skip header row
      try{
        if (csvData[rowIndex].length < 3) {
          throw Exception('Invalid model data.');
        }
        int classId = int.parse(csvData[rowIndex][0].toString());
        String labelName = csvData[rowIndex][1].toString();
        String csvSeverity = csvData[rowIndex][2].toString();

        alertConfig[classId] = LabelConfig(
          labelName,
          true,
          Severity.fromString(csvSeverity),
          //TODO: implement dynamic action set
          ModelConstants.DEFAULT_ACTION_SET,
        );
      } catch (e) {
        throw Exception('Error parsing CSV data at row $rowIndex: $e');
      }
    }
    return AlertConfig(classCount, alertConfig);
  }
}

/**
 * Data structure for a sound label config
 * + E - enablility - bool
 * + S - severity   - Enum
 * + A - actions    - List\<Enum\>
 */
class LabelConfig {
  final String labelName;
  final bool e;
  final Severity s;
  final Set<Action> a;

  LabelConfig(this.labelName, this.e, this.s, this.a);

  @override
  String toString() {
    return 'LabelConfig{e: $e, s: $s}';
  }
}