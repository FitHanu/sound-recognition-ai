import 'package:danger_sound_recognition/src/features/record/domain/enums.dart';
import 'package:danger_sound_recognition/src/features/record/domain/model_meta.dart';
import 'package:danger_sound_recognition/src/utils/json_utils.dart';

class ModelConstants {
  static const String DEFAULT_CLASS_CONFIG_CSV = 'classes_default_config.csv';
  static const String PROJECT_REL_MODEL_PATH = 'assets/models/';
  static const String MODEL_TYPE_TFLITE = 'tflite';
  static const String MODELS_JSON = 'assets/models.json';

  static final Future<ClassificationModelMeta> DEFAULT_MODEL =
      JsonUtils.getModelMeta(MODELS_JSON);
  static final Set<Action> DEFAULT_ACTION_SET = {
    Action.LOG_HISTORY,
    Action.ALERT,
    Action.NOTIFY,
  };
}

class RegisteredModeKey {
  static const String YAMNET = 'yamnet';
  static const String YAMNET_TWEAKED = 'yamnet_tweaked';
}
