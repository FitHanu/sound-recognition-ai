package org.fit.sra;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final int VERSION = Build.VERSION.SDK_INT;

    /**
     * Layouts
     */
    private TextView tvClassifier, tvStatus;
    private Button bRecord;

    /**
     * State
     */
    private boolean isRecording = false;
    AudioClassifier classifier;
    private TensorAudio tensor;
    private AudioRecord record;
    private TimerTask timerTask;
    private final float probabilityThreshold = 0.3f;

    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize all variables with their layout items.
        this.tvStatus = findViewById(R.id.tvStatus);
        this.tvClassifier = findViewById(R.id.tvClassifier);
        this.bRecord = findViewById(R.id.bRecord);
        // Apply the theme based on the current mode
        int currentNightMode = getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            this.tvStatus.setTextColor(Color.WHITE);
            this.tvClassifier.setTextColor(Color.WHITE);
            this.bRecord.setTextColor(Color.WHITE);
        }

        try {
            String modelPath = getString(R.string.model_path);
            classifier = AudioClassifier.createFromFile(this, modelPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.tensor = classifier.createInputTensorAudio();
        TensorAudio.TensorAudioFormat format = classifier.getRequiredTensorAudioFormat();
        String specs = "Number of channels: " + format.getChannels() + "\n" + "Sample Rate: " +
                format.getSampleRate();
        Log.d("Input format", specs);

        bRecord.setOnClickListener(v -> {
            if (!checkPermission()) {
                requestPermissions();
            }
            this.isRecording = !isRecording;
            updateLayout();
            handleRecordButton();
        });
    }

    private void updateLayout() {
        //handle change layout
        if (isRecording) {
            this.bRecord.setText(R.string.button_recording);
            this.tvStatus.setText(R.string.status_recoding);
        } else {
            this.bRecord.setText(R.string.button_initial);
            this.tvStatus.setText(R.string.status_initial);
            this.tvClassifier.setText(R.string.classifier_initial);

        }
    }

    private void handleRecordButton() {
        if (this.isRecording) {
            this.record = this.classifier.createAudioRecord();
            this.record.startRecording();
            this.timerTask = new TimerTask() {
                @Override
                public void run() {
                    // Classifying audio data
                    // val numberOfSamples = tensor.load(record)
                    // val output = classifier.classify(tensor)
                    int numberOfSamples = tensor.load(record);
                    List<Classifications> output = classifier.classify(tensor);

                    // Filtering out classifications with low probability
                    List<Category> finalOutput = new ArrayList<>();
                    for (Classifications classifications : output) {
                        for (Category category : classifications.getCategories()) {
                            if (category.getScore() > probabilityThreshold) {
                                finalOutput.add(category);
                            }
                        }
                    }

                    // Sorting the results
                    finalOutput.sort((o1, o2) -> (int) (o1.getScore() - o2.getScore()));

                    // Creating a multiline string with the filtered results
                    StringBuilder outputStr = new StringBuilder();
                    for (Category category : finalOutput) {
                        outputStr
                                .append(category.getLabel())
                                .append(": ")
                                .append(category.getScore())
                                .append("\n");
                    }

                    // Updating the UI
                    runOnUiThread(() -> {
                        if (finalOutput.isEmpty()) {
                            MainActivity.this.tvClassifier.setText(R.string.classifier_initial);
                        } else {
                            MainActivity.this.tvClassifier.setText(outputStr.toString());
                        }
                    });
                }
            };

            int TASK_PERIOD_MS = 500;
            int TASK_DELAY_MS = 1;
            new Timer().schedule(timerTask, TASK_DELAY_MS, TASK_PERIOD_MS);
        } else {
            timerTask.cancel();
            record.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Permission", "Required permissions: " + Arrays.toString(permissions));
        Log.d("Permission", "Granted permission results: " + Arrays.toString(grantResults));
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord =
                            grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore =
                            grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast
                                .makeText(getApplicationContext(),
                                        "Permission Granted",
                                        Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast
                                .makeText(getApplicationContext(),
                                        "Permission Denied",
                                        Toast.LENGTH_LONG)
                                .show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        boolean hasRecordPermission = this.checkRecordPermission();
        boolean hasWritePermission = this.checkWritePermission();
        if (!hasWritePermission) {
            Log.w("Permission", "No write permission.");
        }
        if (!hasRecordPermission) {
            Log.w("Permission", "No record permission.");
        }
        return hasRecordPermission && hasWritePermission;
    }

    private boolean checkWritePermission() {
        boolean hasWritePermission;
        if (this.VERSION <= 32) {
            hasWritePermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                    WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            File filesDir = getFilesDir();
            hasWritePermission = filesDir.exists() && filesDir.canWrite();
        }
        return hasWritePermission;
    }

    private boolean checkRecordPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (!this.checkRecordPermission()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{RECORD_AUDIO},
                    REQUEST_AUDIO_PERMISSION_CODE);
        }
        if (!this.checkWritePermission()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    REQUEST_AUDIO_PERMISSION_CODE);
        }
    }

}