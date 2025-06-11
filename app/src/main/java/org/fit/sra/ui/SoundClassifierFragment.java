package org.fit.sra.ui;
import androidx.core.content.ContextCompat;

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
import androidx.fragment.app.Fragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.List;
import org.fit.sra.R;
import org.fit.sra.service.SoundClassifierService;
import org.fit.sra.state.AppStateManager;
import org.fit.sra.util.CategoryUtils;
import org.fit.sra.util.CommonUtils;
import org.fit.sra.model.CategoryWithSeverity;

public class SoundClassifierFragment
    extends Fragment
    implements AppStateManager.RecordingStateListener,
               AppStateManager.CategoryStateListener {

  private TextView tvClassifier, tvStatus;
  private ConstraintLayout clClassifier;
  private Button bRecord;
  private AppStateManager stateManager;

  private SoundClassifierService classifierService;

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
    AppStateManager.getInstance().addIsRecordListener(this);
    AppStateManager.getInstance().addCategoriesListener(this);


    tvStatus = view.findViewById(R.id.tvStatus);
    tvClassifier = view.findViewById(R.id.tvClassifier);
    clClassifier = view.findViewById(R.id.my_container);
    bRecord = view.findViewById(R.id.bRecord);

    // UI Mode theming
    int currentNightMode = requireContext().getResources().getConfiguration().uiMode &
        android.content.res.Configuration.UI_MODE_NIGHT_MASK;
    if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
      tvStatus.setTextColor(Color.WHITE);
      tvClassifier.setTextColor(Color.WHITE);
      bRecord.setTextColor(Color.WHITE);
    }

    // Get classifierService from activity
    classifierService = ((ClassifierProvider) requireActivity()).getSoundClassifierService();

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
      List<CategoryWithSeverity> categories = stateManager.getRecognitionCategories();
      String outputText = CategoryUtils.convertString(categories);
      String severity = CategoryUtils.getSeverity(categories);

      int color;
      switch (severity.toUpperCase()) {
          case "NONE":
            color = ContextCompat.getColor(requireContext(), R.color.severity_none); // gray from colors.xml
          break;
          case "LOW":
          color = ContextCompat.getColor(requireContext(), R.color.severity_low); // green
          break;
          case "MEDIUM":
          color = ContextCompat.getColor(requireContext(), R.color.severity_medium); // yellow
          break;
          case "HIGH":
          color = ContextCompat.getColor(requireContext(), R.color.severity_high); // red
          break;
          default:
          color = ContextCompat.getColor(requireContext(), R.color.severity_others);// blue gray
          break;
      }

      // clClassifier.setBackgroundColor(Color.parseColor("#90A4AE"));
      clClassifier.setBackgroundColor(color);
      tvClassifier.setText(outputText);
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
    AppStateManager.getInstance().removeListener(this);
  }

  @Override
  public void onRecordingStateChanged(boolean isRecording) {
    Log.d("", "onRecordingStateChanged :" + isRecording);
    updateUI();
  }

  @Override
  public void onCategoryStateChanged(List<CategoryWithSeverity> categories) {
    String opStr = "";
    if (!CommonUtils.isListNullOrEmpty(categories)) {
      opStr = categories.toString();
    }
    Log.d("", "onCategoryStateChanged :" + opStr);
    updateCategoriesUI();
  }

  public interface ClassifierProvider {
    SoundClassifierService getSoundClassifierService();
    org.fit.sra.service.PermissionService getPermissionService();
  }

}
