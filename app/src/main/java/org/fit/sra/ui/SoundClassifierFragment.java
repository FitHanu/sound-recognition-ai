package org.fit.sra.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.List;
import org.fit.sra.DangerLevel;
import org.fit.sra.R;
import org.fit.sra.constant.AppConst;
import org.fit.sra.service.CategorySeverityFilterService;
import org.fit.sra.service.SoundClassifierService;
import org.fit.sra.state.AppStateManager;
import org.fit.sra.util.CommonUtils;
import org.tensorflow.lite.support.label.Category;

public class SoundClassifierFragment
    extends Fragment
    implements AppStateManager.RecordingStateListener,
               AppStateManager.CategoryStateListener {

  private TextView tvSoundEventInMomentClassName, tvClassifier, tvStatus;
  private ConstraintLayout clClassifier;
  private Button bRecord;
  private AppStateManager stateManager;
  private SoundClassifierService classifierService;
  private CategorySeverityFilterService categoryService;


  private Runnable resetUIRunnable;
  private final android.os.Handler uiHandler = new android.os.Handler();

  private boolean isHoldBackground;
  private final int MIN_HOLD_SEVERITY = DangerLevel.LOW.getValue();
  private int holdSeverity = MIN_HOLD_SEVERITY;

  public SoundClassifierFragment() {}

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState
  ) {
    return inflater.inflate(R.layout.activity_sound_classifier, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Init state
    this.stateManager = AppStateManager.getInstance();


    tvStatus = view.findViewById(R.id.tvStatus);
    tvClassifier = view.findViewById(R.id.tvClassifier);
    tvSoundEventInMomentClassName = view.findViewById(R.id.tv_sound_event_in_moment);
    clClassifier = view.findViewById(R.id.my_container);
    bRecord = view.findViewById(R.id.bRecord);

    tvClassifier.setTextColor(Color.BLACK);
    bRecord.setTextColor(Color.WHITE);


    // Get classifierService from activity
    classifierService = ((ClassifierProvider) requireActivity()).getSoundClassifierService();
    categoryService = CategorySeverityFilterService.getTheInstance(getContext());

    bRecord.setOnClickListener(v -> {
      if (((ClassifierProvider) requireActivity()).getPermissionService().hasPermissionNotGranted()) {
        ((ClassifierProvider) requireActivity()).getPermissionService().requestAllPermissions();
        return;
      }

      stateManager.toggleRecoding();
      handleRecordButton();
    });

    updateUI();
  }

  private void updateUI() {
    if (stateManager.isRecording()) {
      bRecord.setText(R.string.button_recording);
      tvStatus.setText(R.string.status_recoding);
    } else {
      bRecord.setText(R.string.button_initial);
      tvStatus.setText(R.string.status_initial);
      tvClassifier.setText(R.string.classifier_initial);
      clClassifier.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }
  }

  private void updateCategoriesUI() {
    if (stateManager.isRecording()) {
      List<Category> categories = stateManager.getRecognitionCategories();
      Category topCategory = (!categories.isEmpty())
          ? categories.get(0)
          : AppConst.SILENCE_YAMNET;
      int id = topCategory.getIndex();
      DangerLevel dangerLevel = categoryService.getDangerLevelById(id);
      String label = categoryService.getAlternateClassNameById(id);
      String severity = dangerLevel.name();
      String outputText = "Event: " + label
          + ", Level: " + severity
          + ", Confidence (%): " + topCategory.getScore();

      int color;
      int delayMillis = 0;

      switch (severity) {
        case "NONE":
          color = ContextCompat.getColor(requireContext(), R.color.severity_none);   // white
          break;
        case "LOW":
          color = ContextCompat.getColor(requireContext(), R.color.severity_low);    // green
          delayMillis = 1500; // 1.5 seconds
          break;
        case "MEDIUM":
          color = ContextCompat.getColor(requireContext(), R.color.severity_medium); // yellow
          delayMillis = 3000; // 3 seconds
          break;
        case "HIGH":
          color = ContextCompat.getColor(requireContext(), R.color.severity_high);   // red
          delayMillis = 5000; // 5 seconds
          break;
        default:
          color = ContextCompat.getColor(requireContext(), R.color.severity_others); // blue gray
          break;
      }

      Log.d("", "OUTPUT TEXT: " + outputText);
      tvClassifier.setText(outputText);

      boolean isSeverityGreaterOrEqual = (dangerLevel.getValue() >= this.holdSeverity);
      if (delayMillis > 0 && isSeverityGreaterOrEqual) {
        // Cancel any pending reset
        if (resetUIRunnable != null) {
          uiHandler.removeCallbacks(resetUIRunnable);
        }
        this.isHoldBackground = true;
        this.holdSeverity = dangerLevel.getValue();
        this.tvSoundEventInMomentClassName.setText(CommonUtils.getMomentSoundEventText(label));
        clClassifier.setBackgroundColor(color);
        resetUIRunnable = () -> {
          this.isHoldBackground = false;
          this.holdSeverity = MIN_HOLD_SEVERITY;
          this.tvSoundEventInMomentClassName.setText(R.string.classifier_initial);
        };
        uiHandler.postDelayed(resetUIRunnable, delayMillis);
      }

      // Update if bg color is not holding
      if (!this.isHoldBackground) {
        clClassifier.setBackgroundColor(color);
      }
    }
  }


  private void handleRecordButton() {
    if (stateManager.isRecording()) {
      classifierService.start();
    } else {
      classifierService.stop();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    AppStateManager.getInstance().removeIsRecordingListener(this);
    AppStateManager.getInstance().removeCategoryListener(this);
    if (resetUIRunnable != null) {
      uiHandler.removeCallbacks(resetUIRunnable);
    }
  }

  @Override
  public void onRecordingStateChanged(boolean isRecording) {
    Log.d("", "onRecordingStateChanged :" + isRecording);
    updateUI();
  }

  @Override
  public void onCategoryStateChanged(List<Category> categories) {
    updateCategoriesUI();
  }

  @Override
  public void onStop() {
    super.onStop();
    AppStateManager.getInstance().removeIsRecordingListener(this);
    AppStateManager.getInstance().removeCategoryListener(this);
  }

  @Override
  public void onStart() {
    super.onStart();
    AppStateManager.getInstance().addCategoriesListener(this);
    AppStateManager.getInstance().addIsRecordListener(this);
  }



  public interface ClassifierProvider {
    SoundClassifierService getSoundClassifierService();
    org.fit.sra.service.PermissionService getPermissionService();
  }

}
