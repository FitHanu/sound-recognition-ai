import os
import wave

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