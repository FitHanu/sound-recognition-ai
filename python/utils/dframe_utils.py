import matplotlib.pyplot as plt
import constants as C
import os
import pandas as pd
import shutil
import tensorflow as tf
from utils.file_utils import get_filename_from_path
import tensorflow_hub as hub
import threading

from logging_cfg import get_logger
l = get_logger(__name__)


# class YamInferResult:
#     _SCORE = 0
#     _EMBEDDINGS = 1
#     _SPECTROGRAM = 2
#     def __init__(self, scores, embeddings, spectrogrram):
#         self.data = [scores, embeddings, spectrogrram]
        
    
#     def __getitem__(self, key):
#         if (key < 0 or key > 2):
#             raise IndexError("Index out of range")
#         return self.data[key]

#     def __str__(self):
#         return f"YamInferResult(scores={self.scores}, embeddings={self.embeddings}, spectrogrram={self.spectrogrram})"

#     def __repr__(self):
#         return self.__str__()

class YamnetWrapper:
    _instance = None
    _lock = threading.Lock()
    SIG_SCORE = "output_0"

    def __new__(cls):
        with cls._lock:
            if cls._instance is None:
                cls._instance = super(YamnetWrapper, cls).__new__(cls)
                cls._instance._model = None
                cls._instance._model_url = C.YAMNET_MODEL_URL
        return cls._instance

    def _load_model(self):
        """Lazy loads the TensorFlow Hub model when first accessed."""
        if self._model is None:
            l.info("Loading model initially...")
            self._model = hub.load(self._model_url)
            # class_map_path = self._model.class_map_path().numpy().decode('utf-8')
            # self.class_names = list(pd.read_csv(class_map_path)['display_name'])
            l.info("Model loaded.")

    def infer(self, inputs: tf.Tensor):
        """Runs inference using the Hub model."""
        self._load_model()
        # scores, embeddings, spectrogram
        return self._model(inputs)

    def extract_scores(self, input: tf.Tensor):
        self._load_model()
        scores, _, _ = self._model(input)
        return scores

    def extract_embedding(self, inputs: tf.Tensor):
        self._load_model()
        _, embeddings, _ = self._model(inputs)
        return embeddings
    
    def extract_spectrogram(self, input: tf.Tensor):
        self._load_model()
        _, _, spectrogram = self._model(input)
        return spectrogram

    def infer_score_class_name(self, inputs: tf.Tensor):
        """_summary_

        Args:
            inputs (tf.Tensor): _description_

        Returns:
            _type_: class_name: str, score: float
        """
        scores = self.extract_scores(inputs)
        class_scores = tf.reduce_mean(scores, axis=0)
        top_class = tf.math.argmax(class_scores)
        top_score = class_scores[top_class].numpy()
        # return self.class_names[top_class], top_score
        return top_class, top_score


def plot_classname_distribution(df: pd.DataFrame):
    """
    Plot the distribution of class names in a dataframe
    """

    # Count the occurrences of each class
    class_counts = df[C.DF_CLASS_NAME_COL].value_counts()

    # Plot the bar chart
    plt.figure(figsize=(12, 6))
    class_counts.plot(kind="bar", color="skyblue", edgecolor="black")

    # Formatting the plot
    plt.title("Number of Data Points per Class", fontsize=14)
    plt.xlabel("Class Name", fontsize=12)
    plt.ylabel("Number of Data Points", fontsize=12)
    plt.xticks(rotation=45, ha="right")
    plt.grid(axis="y", linestyle="--", alpha=0.7)

    # Show the plot
    plt.show()

def copy_update_dataset_file(df: pd.DataFrame, dest_path: str) -> pd.DataFrame:
    """
    Copy dataset files from df to the given path\n
    Then, return a copy of the DataFrame with updated file paths.
    """
    cp_df = df.copy()
    os.makedirs(dest_path, exist_ok=True)
    shutil.rmtree(dest_path)
    os.makedirs(dest_path, exist_ok=True)
    
    # Copy files
    new_paths = []
    for old_path in cp_df["file_path"]:
        try:
            org_filename = get_filename_from_path(old_path)
            new_file_path = os.path.join(dest_path, org_filename)
            shutil.copy2(old_path, new_file_path)
            new_paths.append(new_file_path)
        except FileNotFoundError:
            new_paths.append("FILE_NOT_FOUND")

    # Update paths
    cp_df["file_path"] = new_paths
    return cp_df


def to_tensor_dataset(df: pd.DataFrame) -> tf.data.Dataset:

    """
    convert df to tensorflow compatible dataset     
    """

    from utils.wav_utils import load_wav_16k_mono_3
    
    def transform_wav(filename: str, class_id, fold):
        return load_wav_16k_mono_3(filename), class_id, fold
    
    filenames = df[C.DF_PATH_COL]
    targets = df[C.DF_CLASS_ID_COL]
    folds = df[C.DF_FOLD_COL]

    ts_ds = tf.data.Dataset.from_tensor_slices((filenames, targets, folds))
    return ts_ds.map(transform_wav)
    # return ts_ds


def to_tensor_ds_embedding_extracted(dataset) -> tf.data.Dataset:
    if type(dataset) == pd.DataFrame:
        dataset = to_tensor_dataset(dataset)
    # print(type(dataset))
    # if type(dataset) != tf.data.Dataset:
    #     raise ValueError("Invalid dataset type, expect pd.DataFrame or tf.data.Dataset")

    def extract_embedding(wav_data, label, fold):
        ''' run YAMNet to extract embedding from the wav data '''
        yamnet = YamnetWrapper()
        embeddings = yamnet.extract_embedding(wav_data)
        num_embeddings = tf.shape(embeddings)[0]
        return (embeddings,
                    tf.repeat(label, num_embeddings),
                    tf.repeat(fold, num_embeddings))

    # extract embedding
    return dataset.map(extract_embedding).unbatch()
    