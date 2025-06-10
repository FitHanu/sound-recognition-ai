package org.fit.sra.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.fit.sra.R;
import org.fit.sra.service.SoundClassifierService;

public class SoundClassifierFragment extends Fragment {

  private TextView tvClassifier, tvStatus;
  private Button bRecord;
  private boolean isRecording = false;

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

      isRecording = !isRecording;
      updateUI();
      handleRecordButton();
    });

    updateUI();
  }

  private void updateUI() {
    if (isRecording) {
      bRecord.setText(R.string.button_recording);
      tvStatus.setText(R.string.status_recoding);
    } else {
      bRecord.setText(R.string.button_initial);
      tvStatus.setText(R.string.status_initial);
      tvClassifier.setText(R.string.classifier_initial);
    }
  }

  private void handleRecordButton() {
    if (isRecording) {
      classifierService.start();
    } else {
      classifierService.stop();
    }
  }

  public interface ClassifierProvider {
    SoundClassifierService getSoundClassifierService();
    org.fit.sra.service.PermissionService getPermissionService();
  }
}
