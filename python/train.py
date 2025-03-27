import os
from csv_utils import get_classes_from_config
import keras
from metric_utils import f1_score
import pandas as pd
import constants as C
import traceback
import tensorflow as tf
import tensorflow_hub as hub

from utils.dframe_utils import to_tensor_ds_embedding_extracted
from logging_cfg import get_logger
l = get_logger(__name__)


def main():
    df = pd.read_csv(C.FILTERED_AUG_FOLDED_META_CSV)
    ds_ts = to_tensor_ds_embedding_extracted(df)

    # Filter train, val, test by fold label
    cached_ds = ds_ts.cache()
    train_ds = cached_ds.filter(lambda embedding, class_name, fold: fold < 8)
    val_ds = cached_ds.filter(lambda embedding, class_name, fold: fold == 8)
    test_ds = cached_ds.filter(lambda embedding, class_name, fold: fold == 9)
    
    # Remove fold column
    remove_fold_column = lambda embedding, class_name, fold: (embedding, class_name)
    train_ds = train_ds.map(remove_fold_column)
    val_ds = val_ds.map(remove_fold_column)
    test_ds = test_ds.map(remove_fold_column)
    
    train_ds = train_ds.cache().shuffle(1000).batch(32).prefetch(tf.data.AUTOTUNE)
    val_ds = val_ds.cache().batch(32).prefetch(tf.data.AUTOTUNE)
    test_ds = test_ds.cache().batch(32).prefetch(tf.data.AUTOTUNE)
    
    # Model setup
    class_names = get_classes_from_config()
    yamnet_tweaked = tf.keras.Sequential([
    tf.keras.layers.Input(shape=(1024, ), dtype=tf.float32, name='input_embedding'),
    tf.keras.layers.Dense(512, activation='relu'),
    tf.keras.layers.Dense(len(class_names))
    ], name='yamnet_tweaked')

    yamnet_tweaked.summary()
    
    # Compile the model
    # raw scores (logits) instead of probabilities (if the final layer doesn’t have softmax).
    yamnet_tweaked.compile(loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
                            optimizer="adamax",
                            metrics=[
                                keras.metrics.Precision(name="precision"),  
                                keras.metrics.Recall(name="recall"),
                                f1_score
                            ])

    callback = tf.keras.callbacks.EarlyStopping(monitor='loss',
                                                patience=5,
                                                restore_best_weights=True)

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
    
    # saved_model_path = os.path.join(C.MODELS_PATH, "yamnet_tweaked")
    # input_segment = tf.keras.layers.Input(shape=(), dtype=tf.float32, name='audio')
    # embedding_extraction_layer = hub.KerasLayer(C.YAMNET_MODEL_URL,
    #                                             trainable=False, name='yamnet')
    # _, embeddings_output, _ = embedding_extraction_layer(input_segment)
    # serving_outputs = yamnet_tweaked(embeddings_output)
    # serving_outputs = ReduceMeanLayer(axis=0, name='classifier')(serving_outputs)
    # serving_model = tf.keras.Model(input_segment, serving_outputs)
    # l.info(f"Model summary:")
    # serving_model.summary()
    # l.info(f"Saving model...")
    # serving_model.save(saved_model_path, include_optimizer=False)
    # l.info(f"Model saved to {saved_model_path}")
    

if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        l.error(f"Error while executing train script: {e}")
        l.error(f"{traceback.print_exc()}")
        l.info(f"Exiting with code 1, full log saved to {C.LOG_PATH}")
        exit(1)
    # test()