package org.fit.sra.service;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Arrays;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PermissionService {

    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private final Activity activity;
    private final int androidVersion;

    public PermissionService(Activity activity) {
        this.activity = activity;
        this.androidVersion = Build.VERSION.SDK_INT;
    }

    public boolean hasPermissionNotGranted() {
        boolean hasRecordPermission = checkRecordPermission();
        boolean hasWritePermission = checkWritePermission();

        if (!hasWritePermission) {
            Log.w("Permission", "No write permission.");
        }
        if (!hasRecordPermission) {
            Log.w("Permission", "No record permission.");
        }

        return !hasRecordPermission || !hasWritePermission;
    }

    public void requestAllPermissions() {
        if (!checkRecordPermission()) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{RECORD_AUDIO},
                    REQUEST_AUDIO_PERMISSION_CODE);
        }

        if (!checkWritePermission()) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    REQUEST_AUDIO_PERMISSION_CODE);
        }
    }

    private boolean checkWritePermission() {
        if (androidVersion <= 32) {
            return ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            File filesDir = activity.getFilesDir();
            return filesDir.exists() && filesDir.canWrite();
        }
    }

    private boolean checkRecordPermission() {
        return ContextCompat.checkSelfPermission(activity, RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void handlePermissionsResult(int requestCode,
                                        @NonNull String[] permissions,
                                        @NonNull int[] grantResults) {
        Log.d("Permission", "Required permissions: " + Arrays.toString(permissions));
        Log.d("Permission", "Granted permission results: " + Arrays.toString(grantResults));

        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean permissionToRecord =
                        grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean permissionToStore =
                        grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(activity, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

