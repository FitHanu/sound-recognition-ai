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
import androidx.fragment.app.Fragment;
import java.util.List;
import org.fit.sra.R;
import org.fit.sra.service.SoundClassifierService;
import org.fit.sra.state.AppStateManager;
import org.fit.sra.util.CategoryUtils;
import org.fit.sra.util.CommonUtils;
import org.tensorflow.lite.support.label.Category;

public class SoundClassifierFragment
    extends Fragment
    implements AppStateManager.RecordingStateListener,
               AppStateManager.CategoryStateListener {

  private TextView tvClassifier, tvStatus;
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
    }
  }

  private void updateCategoriesUI() {
    if (stateManager.isRecording()) {
      List<Category> categories = stateManager.getRecognitionCategories();
      String outputText = CategoryUtils.convertString(categories);
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
  public void onCategoryStateChanged(List<Category> categories) {
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
