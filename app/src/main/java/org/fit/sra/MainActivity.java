package org.fit.sra;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.pm.PackageManager;
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
import androidx.lifecycle.ViewModelProvider;

import org.fit.sra.data.AppViewModel;
import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

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



    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppViewModel appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        // initialize all variables with their layout items.
        this.tvStatus = findViewById(R.id.tvStatus);
        this.tvClassifier = findViewById(R.id.tvClassifier);
        this.bRecord = findViewById(R.id.bRecord);


        try {
            String modelPath = getString(R.string.model_path);
            classifier = AudioClassifier.createFromFile(this, modelPath);
            tvClassifier.setText(classifier.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        appViewModel.getData().observe(this, data -> {
//            int lastPos = data.size();
//            mainRecogText.setText(data.get(lastPos - 1));
//        });

        bRecord.setOnClickListener(v -> {
            if (!checkPermission()) {
                return;
            }
            this.isRecording = !isRecording;
            updateLayout();
            handleRecordButton();
        });
    }

    private void updateLayout() {
        //handle button logic
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
        if (!hasRecordPermission) {
            Log.w("Permission", "No record permission.");
        }
        boolean hasWritePermission;
        Log.d("Permission", "Checking permissions ...");
        int version = Build.VERSION.SDK_INT;
        Log.d("Permission", "SDK " + version);
        if (version <= 32) {
            hasWritePermission = this.checkWritePermission32();
        } else {
//            hasWritePermission = Environment.isExternalStorageManager(getFilesDir());
            File filesDir = getFilesDir();
            hasWritePermission =  filesDir.exists() && filesDir.canWrite();
        }
        if (!hasWritePermission) {
            Log.w("Permission", "No write permission.");
        }
        return hasRecordPermission && hasWritePermission;
    }

    private boolean checkWritePermission32() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ;
    }

    private boolean checkRecordPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE},
                REQUEST_AUDIO_PERMISSION_CODE);
    }

}