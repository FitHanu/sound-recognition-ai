"""
The project entry script
"""

import os
import pandas as pd
import constants as C
import tensorflow as tf
import uuid
import traceback
import tensorflow_hub as hub
from tensorflow import keras
from constants import PROJECT_ROOT
from ds.dataset import PD_SCHEMA
from utils.json_utils import init_default_class_name, append_empty_mapping_to_config
from utils.file_utils import init_class_folds, get_filename_without_extension
from utils.csv_utils import read_csv_as_dataframe, write_csv_meta
from utils.dframe_utils import plot_classname_distribution, copy_update_dataset_file
from utils.wav_utils import (convert_pcm_pd_row,
                            convert_pcm_pd_row_2,
                            validate_wav_pd_row)
from partition.split_tdt import split_tdt, init_cfg
from ds.esc50 import ESC50
from ds.us8k import UrbanSound8K
from ds.bdlib2 import BDLib2
from ds.gad import GAD

from logging_cfg import get_logger
l = get_logger(__name__)

def workflow():
    """
    Main procedure
    """
    datasets_registry = [
        ESC50(),
        GAD(),
        UrbanSound8K(),
        BDLib2(),
    ]

    # Init paths, Default class names
    # DATASET_PATH_FILTERED = os.path.join(PROJECT_ROOT, "dataset")
    l.info(f"Creating empty dataset directory to {C.FILTERED_DATASET_PATH}")
    os.makedirs(C.FILTERED_DATASET_PATH, exist_ok=True)
    init_default_class_name()
    # init_class_folds(DATASET_PATH_FILTERED)

    # Init main dataframe
    main_df = pd.DataFrame(columns=PD_SCHEMA.keys()).astype(PD_SCHEMA)

    # Process each dataset
    for ds in datasets_registry:
        # Add empty mapping to config (initial)
        l.info(f"Filtering & mapping class names for {ds.key}")
        append_empty_mapping_to_config(ds, overwrite=False)

        # Call ds life cycle methods
        ds.hell_yeah()
        l.info(f'Dataset "{ds.name}" info saved to {C.FILTERED_DATASET_PATH}')
        # Read filtered metafile
        df = read_csv_as_dataframe(ds.get_filtered_meta_path())

        # Append to main dataframe
        main_df = pd.concat([main_df, df], ignore_index=True)
        l.info(f"main_df shape after filter: {main_df.shape}")

    l.info(f"Done filtering & mapping class names for all datasets")
    l.info(f"Main dataframe shape: {main_df.shape}")

    # Write main dataframe to csv

    l.info(f"Writing filtered merged meta file into: {C.MERGED_META_CSV}")
    write_csv_meta(main_df, "merged")
    
    l.info(f"Validating .wav files from merged dataset, path: {C.FILTERED_DATASET_PATH}")
    
    # Count missing files after filtering
    missing_files = main_df[main_df[C.DF_PATH_COL].apply(os.path.isfile)]
    if missing_files.shape[0] > 0:
        l.warning(f"Missing files: {missing_files.shape[0]}")
        missing_files.to_csv(
            C.PY_PROJECT_ROOT + os.path.sep + "missing_files.csv", index=False
        )

    l.info(f"1st. Converting .wav files into PCM format inside {C.FILTERED_DATASET_PATH} using ffmpeg...")
    main_df.apply(convert_pcm_pd_row, axis=1)

    l.info(f"2nd. Converting .wav files into PCM format inside {C.FILTERED_DATASET_PATH} using sox...")
    main_df.apply(convert_pcm_pd_row_2, axis=1)

    # Filter invalid WAV files (not PCM)
    false_files = main_df[~main_df.apply(validate_wav_pd_row, axis=1)]
    
    l.info(f"Done validating and converting .wav files")
    
    if false_files.shape[0] > 0:
        l.warning(f"Theres still {false_files.shape[0]}/{main_df.shape[0]} invalid .wav files after conversion, dropping them...")
        main_df = main_df[~main_df.index.isin(false_files.index)]
        l.info(f"Dataset shape after dropping invalid .wav files: {main_df.shape}")
        
    # no_length_row = ds.df[ds.df[C.DF_LENGTH_COL] == -1]
    # l.warning(f"Could not get .wav length for {len(no_length_row)} row(s)")
    # l.warning(no_length_row)

    # Get split config
    cfg = init_cfg()

    l.info(f"Spliting with cfg {cfg.__str__()}")

    # Split
    aug_k_df = split_tdt(main_df, cfg)

    # Save augmented dataframe to .csv
    final_meta = C.FILTERED_AUG_FOLDED_META_CSV
    l.info(f"Datasets processing done, saving meta file to {final_meta}")
    aug_k_df.to_csv(final_meta, index=False)
    # from utils.dframe_utils import to_tensor_ds_embedding_extracted
    # ds_ts = to_tensor_ds_embedding_extracted(aug_k_df)

    # cached_ds = ds_ts.cache()
    # train_ds = cached_ds.filter(lambda embedding, class_name, fold: fold < 8)
    # val_ds = cached_ds.filter(lambda embedding, class_name, fold: fold == 8)
    # test_ds = cached_ds.filter(lambda embedding, class_name, fold: fold == 9)
    
    # remove_fold_column = lambda embedding, class_name, fold: (embedding, class_name)

    # train_ds = train_ds.map(remove_fold_column)
    # val_ds = val_ds.map(remove_fold_column)
    # test_ds = test_ds.map(remove_fold_column)
    
    # train_ds = train_ds.cache().shuffle(1000).batch(32).prefetch(tf.data.AUTOTUNE)
    # val_ds = val_ds.cache().batch(32).prefetch(tf.data.AUTOTUNE)
    # test_ds = test_ds.cache().batch(32).prefetch(tf.data.AUTOTUNE)
    
    # from utils.csv_utils import get_classes_from_config
    # class_names = get_classes_from_config()
    
    # yamnet_tweaked = tf.keras.Sequential([
    # tf.keras.layers.Input(shape=(1024, ), dtype=tf.float32, name='input_embedding'),
    # tf.keras.layers.Dense(512, activation='relu'),
    # tf.keras.layers.Dense(len(class_names))
    # ], name='yamnet_tweaked')

    # yamnet_tweaked.summary()
    
    # from utils.metric_utils import f1_score
    # # raw scores (logits) instead of probabilities (if the final layer doesn’t have softmax).
    # yamnet_tweaked.compile(loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
    #                         optimizer="adamax",
    #                         metrics=[
    #                             keras.metrics.Precision(name="precision"),  
    #                             keras.metrics.Recall(name="recall"),
    #                             f1_score
    #                         ])

    # callback = tf.keras.callbacks.EarlyStopping(monitor='loss',
    #                                             patience=5,
    #                                             restore_best_weights=True)

    # history = yamnet_tweaked.fit(train_ds,
    #                     epochs=100,
    #                     validation_data=val_ds,
    #                     callbacks=callback)
    
    # # Save training history to log directory
    # history_log_path = os.path.join(C.LOG_PATH, "training_history.txt")
    # with open(history_log_path, "w") as f:
    #     for key, values in history.history.items():
    #         f.write(f"{key}: {values}\n")
    # l.info(f"Training history saved to {history_log_path}")
    
    # loss, accuracy = yamnet_tweaked.evaluate(test_ds)
    # l.info(f"Loss: {loss}")
    # l.info(f"Accuracy: {accuracy}")
    
    # class ReduceMeanLayer(tf.keras.layers.Layer):
    #     def __init__(self, axis=0, **kwargs):
    #         super(ReduceMeanLayer, self).__init__(**kwargs)
    #         self.axis = axis

    #     def call(self, input):
    #         return tf.math.reduce_mean(input, axis=self.axis)
    
    # saved_model_path = './yamnet_tweaked'
    # input_segment = tf.keras.layers.Input(shape=(), dtype=tf.float32, name='audio')
    # embedding_extraction_layer = hub.KerasLayer('https://tfhub.dev/google/yamnet/1',
    #                                             trainable=False, name='yamnet')
    # _, embeddings_output, _ = embedding_extraction_layer(input_segment)
    # serving_outputs = yamnet_tweaked(embeddings_output)
    # serving_outputs = ReduceMeanLayer(axis=0, name='classifier')(serving_outputs)
    # serving_model = tf.keras.Model(input_segment, serving_outputs)
    # serving_model.save(saved_model_path, include_optimizer=False)
    
    



def get_args():
    """
    Get arguments
    """
    import argparse

    parser = argparse.ArgumentParser(description="Workflow")
    parser.add_argument(
        "--clean-cache", help="Clean cached dataset processes", action="store_true"
    )
    return parser.parse_args()


def test():
    workflow()
    pth = os.path.join(PROJECT_ROOT, "dataset", "merged.augmented.folded.csv")
    ds = pd.read_csv(pth)
    from utils.dframe_utils import to_tensor_ds_embedding_extracted
    ds_ts = to_tensor_ds_embedding_extracted(ds)
    print(ds_ts)
    print(ds_ts.element_spec)
    for i, (audio_waveform, label, fold) in enumerate(ds_ts.take(10)):
        if tf.reduce_any(tf.math.is_nan(audio_waveform)):
            print(f"⚠️ Found NaN values in audio at index {i}")
        if tf.size(audio_waveform) == 0:
            print(f"⚠️ Empty audio tensor at index {i}")
        file_name = f"{label}_{fold}_{uuid.uuid1()}.png"
        file_name = os.path.join(C.PY_PROJECT_ROOT, "plots", file_name)
        from wav_utils import plot_mono_wav
        plot_mono_wav(audio_waveform, figname=file_name)

if __name__ == "__main__":
    args = get_args()
    if args.clean_cache == True:
        from utils.file_utils import clean_user_cache_dir
        l.info("Cleaning user cache dir ...")
        c_dir = clean_user_cache_dir()
        l.info(f"Contents in {c_dir} has been cleaned.")
    try:
        workflow()
    except Exception as e:
        l.error(f"Error while executing workflow: {e}")
        l.error(f"{traceback.print_exc()}")
        l.info(f"Exiting with code 1, full log saved to {C.LOG_PATH}")
        exit(1)
    # test()

