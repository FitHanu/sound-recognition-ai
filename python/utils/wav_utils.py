import os
import wave
import tensorflow as tf
import pandas as pd
import soundfile as sf
import librosa
import matplotlib.pyplot as plt
import numpy as np
from dataclasses import dataclass
from typing import Optional
import traceback

from logging_cfg import get_logger
l = get_logger(__name__)

@dataclass
class WavInfo:
    sample_rate: int
    num_channels: int
    duration: float
    audio_format: str

class InvalidArgument(Exception):
    pass

def get_wav_data_length(file_path: str) -> float:
    # Validate file path
    if not os.path.isfile(file_path):
        raise InvalidArgument(f"The provided path is not a valid file: {file_path}")
    # Validate file type
    if not file_path.lower().endswith('.wav'):
        raise InvalidArgument(f"The provided file is not a WAV file: {file_path}")
    
    # Read WAV file and calculate length in milliseconds
    try:
        with wave.open(file_path, 'rb') as wav_file:
            frames = wav_file.getnframes()
            rate = wav_file.getframerate()
            duration = frames / float(rate)
            return duration * 1000  # Convert to milliseconds
    except Exception as e:
        print(f"Error processing WAV file: {file_path}, error: {e}")
        raise e


def get_wave_data_length_2(row: pd.Series) -> float:
    """
    For large and possibly falsy dataset, `return -1` for defect .wav file for further processing
    """
    # Validate file path
    import constants as C
    try:
        file_path = row[C.DF_PATH_COL]
        with sf.SoundFile(file_path) as audio_file:
            len = len(audio_file)
            sr = audio_file.samplerate
            duration = len / sr
            return duration * 1000  # Convert to milliseconds
        row[C.DF_LENGTH_COL] = file_length
    except Exception as e:
        l.warning(f"Could not get .wav length for {row[C.DF_PATH_COL]}, {e}")
        traceback.print_exc()
        row[C.DF_LENGTH_COL] = -1
    return row

def load_wav(filename):
    file_contents = tf.io.read_file(filename)
    wav, _ = tf.audio.decode_wav(
        file_contents,
        desired_channels=1)
    return wav

def load_wav_mono(filename):
    wav = load_wav(filename)
    return tf.squeeze(wav, axis=-1)

@tf.function
def load_wav_16k_mono(filename):
    """ Load a WAV file, convert it to a float tensor, resample to 16 kHz single-channel audio. """
    file_contents = tf.io.read_file(filename)
    wav, sample_rate = tf.audio.decode_wav(
        file_contents,
        desired_channels=1)
    wav = tf.squeeze(wav, axis=-1)
    sample_rate = tf.cast(sample_rate, dtype=tf.int64)
    # wav = tfio.audio.resample(wav, rate_in=sample_rate, rate_out=16000)
    return wav

def plot_mono_wav(wav, title="Waveform", sample_rate=16000, figname="plot.png"):
    plt.figure(figsize=(10, 4))
    plt.plot(wav.numpy(), label="Audio Waveform")
    plt.xlabel("Sample Index")
    plt.ylabel("Amplitude")
    plt.title("Audio Waveform")
    plt.legend()
    plt.savefig(figname)

@tf.function
def load_wav_16k_mono_2(filename):
    """Load a WAV file, convert it to a float tensor, and resample using tf.signal.resample."""
    file_contents = tf.io.read_file(filename)
    wav, sample_rate = tf.audio.decode_wav(file_contents, desired_channels=1)
    wav = tf.squeeze(wav, axis=-1)


    target_length = tf.cast(tf.shape(wav)[0] * 16000 / tf.cast(sample_rate, tf.float32), tf.int32)
    wav_resampled = tf.signal.resample(wav, target_length)

    return wav_resampled

@tf.function
def load_wav_16k_mono_3(filename):
    """Load a WAV file, convert it to a float tensor, and resample to 16 kHz single-channel audio using librosa."""
    file_contents = tf.io.read_file(filename)
    wav, sample_rate = tf.audio.decode_wav(file_contents, desired_channels=1)
    wav = tf.squeeze(wav, axis=-1)

    def resample_audio(wav, org_sr):
        # wav = wav.numpy()
        resampled_wav = librosa.resample(y=wav, orig_sr=org_sr, target_sr=16000)
        return resampled_wav.astype(np.float32) 
    
    def tf_resample_audio(wav, org_sr):
        return tf.numpy_function(func=resample_audio, inp=[wav, org_sr], Tout=tf.float32)

    # # Convert to numpy, process with librosa, and convert back to tensor
    # wav_numpy = wav.numpy()
    # wav_resampled = librosa.resample(wav_numpy, orig_sr=sample_rate.numpy(), target_sr=16000)
    
    return tf_resample_audio(wav, sample_rate)

def get_wav_info(file_path: str) -> WavInfo:
    """Extract WAV file information using soundfile."""
    wav_info = sf.info(file_path)
    return WavInfo(
        sample_rate=wav_info.samplerate,
        num_channels=wav_info.channels,
        duration=wav_info.duration,
        audio_format=wav_info.format
    )


def get_wav_info_from_tensor(wav: tf.Tensor) -> WavInfo:
    """Extracts basic WAV info from a tf.float32 tensor."""
    if not isinstance(wav, tf.Tensor) or wav.dtype != tf.float32:
        raise ValueError("Input must be a tf.float32 tensor")

    wav_np = wav.numpy()  # Convert to NumPy for shape processing
    num_channels = 1 if wav_np.ndim == 1 else wav_np.shape[0]
    num_samples = wav_np.shape[-1]

    return WavInfo(
        num_channels=num_channels,
        sample_rate=-1,
        duration=-1,
        audio_format="tf.float32 converted")

# def main():
#     testing_wav_file_name = tf.keras.utils.get_file('miaow_16k.wav',
#                                                 'https://storage.googleapis.com/audioset/miaow_16k.wav',
#                                                 cache_dir='./',
#                                                 cache_subdir='test_data')
#     # wav = load_wav_mono(testing_wav_file_name)
#     wav0 = load_wav(testing_wav_file_name)
#     wav1 = load_wav_mono(testing_wav_file_name)
#     wav2 = load_wav_16k_mono_3(testing_wav_file_name)
#     print(get_wav_info(testing_wav_file_name))
#     print(get_wav_info_from_tensor(wav0))
#     print(get_wav_info_from_tensor(wav1))
#     print(get_wav_info_from_tensor(wav2))
#     os.makedirs("python/plots", exist_ok=True)
#     plot_mono_wav(wav0, figname="python/plots/plot0.png")
#     plot_mono_wav(wav1, figname="python/plots/plot1.png")
#     plot_mono_wav(wav2, figname="python/plots/plot2.png")


# if __name__ == "__main__":
#     main()
