package org.fit.sra.logic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

public class AudioStreamer {

    private static final int SAMPLE_RATE = 16000; // Sample rate in Hz
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT);

    private final AudioRecord audioRecord;
    private boolean isRecording = false;
    private Context context;

    public AudioStreamer(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Audio recording permission not granted !");
        }
        this.context = context;
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                BUFFER_SIZE);
    }

    public void startRecording() {
        audioRecord.startRecording();
        isRecording = true;
        new Thread(new AudioCaptureRunnable()).start();
    }

    public void stopRecording() {
        isRecording = false;
        audioRecord.stop();
    }

    private class AudioCaptureRunnable implements Runnable {

        @Override
        public void run() {
            short[] buffer = new short[BUFFER_SIZE];
            while (isRecording) {
                int read = audioRecord.read(buffer, 0, buffer.length);
                if (read > 0) {
                    // Process the audio data with TensorFlow here
                    processAudioData(buffer, read);
                }
            }
        }

    }

    private void processAudioData(short[] buffer, int read) {
        // Convert short buffer to float buffer for TensorFlow
        float[] floatBuffer = new float[read];
        for (int i = 0; i < read; i++) {
            floatBuffer[i] = buffer[i] / 32768.0f; // Normalize to [-1.0, 1.0]
        }

        // Pass the floatBuffer to TensorFlow for processing
        // Example: tensorFlowModel.run(floatBuffer);
    }

}
