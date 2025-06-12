package org.fit.sra.service;

import android.content.Context;
import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.fit.sra.R;
import org.fit.sra.state.AppStateManager;
import org.fit.sra.util.CsvUtils;
import org.fit.sra.model.CategoryWithSeverity;
import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

  private final AppStateManager stateManager;

 

  public SoundClassifierService(Context context) {
    this.stateManager = AppStateManager.getInstance();
    this.mainHandler = new Handler(Looper.getMainLooper());

    

    try { 
      
      String[] files = context.getAssets().list("models");
      CsvUtils.readSoundCsv(context);
      this.classifier = AudioClassifier
          .createFromFile(context, "models/yamnet_tweaked/pelase.tflite");
           
//          .createFromFile(context, "models/yamnet_tweaked/ydr_m_2025_06_10.tflite");
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

        List<CategoryWithSeverity> filtered = new ArrayList<>();
        
        for (Classifications c : results) {
          //System.out.println(c);
          for (Category cat : c.getCategories()) {
             
              if (cat.getScore() > threshold) {
                
                 int index = cat.getIndex();
                 String severity = CsvUtils.severityByIndex.getOrDefault(index, "Unknown");
                  //filtered.add(cat);
                CategoryWithSeverity wrapped = new CategoryWithSeverity(cat, severity);
                filtered.add(wrapped);
                // System.out.println(wrapped);
              }
          }
        }

        filtered.sort((a, b) -> Float.compare(b.getCat().getScore(), a.getCat().getScore()));

        // Post to main thread
        mainHandler.post(() -> {
          stateManager.setRecognitionCategories(filtered);
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
}
