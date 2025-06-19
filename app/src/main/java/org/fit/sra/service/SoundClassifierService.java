package org.fit.sra.service;

import android.content.Context;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import java.util.Objects;
import org.fit.sra.DangerLevel;
import org.fit.sra.constant.ModelConst;
import org.fit.sra.state.AppStateManager;
import org.fit.sra.model.CategoryWithSeverity;
import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SoundClassifierService {

  private final AudioClassifier classifier;
  private final TensorAudio tensor;
  private final AudioCaptureService audioService;
  private Timer timer;
  private final Handler mainHandler;
  private final float threshold = 0.3f;
  private final FileLoggerService fileLogger;
  private final CategorySeverityFilterService categoryService;

  private final Context appContext;
  private final AppStateManager stateManager;


  public SoundClassifierService(Context context) {
    this.appContext = context;
    this.stateManager = AppStateManager.getInstance();
    this.mainHandler = new Handler(Looper.getMainLooper());
    this.fileLogger = new FileLoggerService(context);
    this.categoryService = CategorySeverityFilterService.getTheInstance(context);

    try {

      this.classifier = AudioClassifier
          .createFromFile(context, ModelConst.MODEL_PATH);
    } catch (IOException e) {

      throw new RuntimeException(e);
    }
    this.audioService = new AudioCaptureService(classifier.createAudioRecord());
    this.tensor = classifier.createInputTensorAudio();
  }

  public void start() {
    this.audioService.start();
    this.timer = new Timer();
    this.timer.schedule(new TimerTask() {
      @Override
      public void run() {
        AudioRecord record = audioService.getRecord();
        tensor.load(record);
        List<Classifications> results;

        try {
          results = classifier.classify(tensor);
        } catch (IllegalStateException | NullPointerException e) {
          Log.e("Classifier", "Failed to classify audio: " + e.getMessage());
          results = List.of();
        }

        List<Category> filtered = new ArrayList<>();

        for (Classifications c : results) {
          for (Category cat : c.getCategories()) {

            if (cat.getScore() > threshold) {

              filtered.add(cat);
            }
          }
        }

        filtered.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));
        DangerLevel dangerLevel = (!filtered.isEmpty()) ?
            categoryService
                .getDangerLevelById(
                    filtered.get(0).getIndex()
                ) : DangerLevel.NONE;

        vibrateOnGivenSeverity(dangerLevel.getDisplayName());

        // Post to main thread
        mainHandler.post(() -> {
          // Set new data for rendering
          stateManager.setRecognitionCategories(filtered);
          // File logger append
          for (Category category : filtered) {
            fileLogger.append(category);
          }
        });
      }
    }, 0, 500);
  }

  public void stop() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
    this.audioService.stop();
  }

  private void vibrateOnGivenSeverity(String severity) {
    // Get instance of Vibrator from current Context
    Vibrator v = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);

    // Output yes if can vibrate, no otherwise
    if (Objects.isNull(v) || !v.hasVibrator()) {
      Log.v("Can Vibrate", "CANNOT VIBRATE, VIBRATOR NOT AVAILABLE");
      return;
    }

    long[] pattern;

    switch (severity.toUpperCase()) {
      case "LOW":
        pattern = new long[] {
            0,
            200, // vibrate
            100,
            200, // vibrate
        };
        break;
      case "MEDIUM":
        pattern = new long[] {
            0,
            300, // vibrate
            100,
            300, // vibrate
            100,
            300, // vibrate
        };
        break;
      case "HIGH":
        pattern = new long[] {
            0,
            350, // vibrate
            100,
            350, // vibrate
            100,
            350, // vibrate
            100,
            350, // vibrate
        };
        break;
      default:
        return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      v.vibrate(VibrationEffect.createWaveform(pattern, -1));
    } else {
      v.vibrate(pattern, -1);
    }
  }
}
