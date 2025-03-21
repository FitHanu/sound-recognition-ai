import os
import wave
import tensorflow as tf
import tensorflow_io as tfio
import pandas as pd
import soundfile as sf

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
    except Exception:
        row[C.DF_LENGTH_COL] = -1
    return row


@tf.function
def load_wav_16k_mono(filename):
    """ Load a WAV file, convert it to a float tensor, resample to 16 kHz single-channel audio. """
    file_contents = tf.io.read_file(filename)
    wav, sample_rate = tf.audio.decode_wav(
        file_contents,
        desired_channels=1)
    wav = tf.squeeze(wav, axis=-1)
    sample_rate = tf.cast(sample_rate, dtype=tf.int64)
    wav = tfio.audio.resample(wav, rate_in=sample_rate, rate_out=16000)
    return wav