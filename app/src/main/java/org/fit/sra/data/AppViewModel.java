package org.fit.sra.data;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppViewModel extends ViewModel {

    private static final int SAMPLE_RATE = 16000; // Sample rate in Hz
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT);
//    private final AudioRecord audioRecord;


    private boolean isRecording = false;
    private final MutableLiveData<List<String>> data = new MutableLiveData<>();

//    public AppViewModel() {
//        this.audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                SAMPLE_RATE,
//                CHANNEL_CONFIG,
//                AUDIO_FORMAT,
//                BUFFER_SIZE);;
//    }

    public LiveData<List<String>> getData() {
        return this.data;
    }

    public void addResult(String result) {
        List<String> d = this.data.getValue();
        if (Objects.isNull(d)) {
            d = new ArrayList<>();
        }
        assert d != null;
        d.add(result);
        data.setValue(d);
        System.gc();
    }

    public void clearData() {
        this.data.setValue(List.of());
    }

}