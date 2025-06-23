package org.fit.sra.constant;

import org.tensorflow.lite.support.label.Category;

public class AppConst {

    private AppConst() {}

    public static final String MODEL_PATH = "models/yamnet/yamnet.tflite";
    public static final String DEFAULT_CONFIG_CSV = "models/yamnet/classes_default_config_filter.csv";
    public static final int SILENCE_EVENT_ID_YAMNET = 494;
    public static final Category SILENCE_YAMNET = Category.create(
        "SILENCE", "SILENCE", 0.8f, SILENCE_EVENT_ID_YAMNET);

    public static final String LOG_DATETIME_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    public static final String LOG_DATETIME_DISPLAY_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LOG_DATA_DATETIME_FORMAT = "HH:mm:ss";
}
