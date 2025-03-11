import os
import wave
import tensorflow as tf
import tensorflow_io as tfio

class InvalidArgument(Exception):
    pass

def get_wav_data_length(file_path: str) -> float:
    # Validate file path
    if not os.path.isfile(file_path):
        raise InvalidArgument("The provided path is not a valid file.")
    # Validate file type
    if not file_path.lower().endswith('.wav'):
        raise InvalidArgument("The provided file is not a WAV file.")
    
    # Read WAV file and calculate length in milliseconds
    try:
        with wave.open(file_path, 'rb') as wav_file:
            frames = wav_file.getnframes()
            rate = wav_file.getframerate()
            duration = frames / float(rate)
            return duration * 1000  # Convert to milliseconds
    except wave.Error as e:
        raise InvalidArgument(f"Error reading WAV file: {e}")
    
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