package org.fit.sra.service;

import android.media.AudioRecord;

/**
 * Handle recording job
 */
public class AudioCaptureService {

    private final AudioRecord record;

    public AudioCaptureService(AudioRecord audioRecord) {
        this.record = audioRecord;
    }

    public void start() {
        this.record.startRecording();
    }

    public void stop() {
        this.record.stop();
    }

    public AudioRecord getRecord() {
        return this.record;
    }

}
