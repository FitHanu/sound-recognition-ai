import tensorflow as tf
from tensorflow.keras import backend as K

# Custom F1-score function
def f1_score(y_true, y_pred):
    y_pred = tf.argmax(y_pred, axis=-1)  # Convert logits to class predictions

    # Compute Precision and Recall manually
    tp = K.sum(K.cast(y_true * y_pred, 'float32'))
    fp = K.sum(K.cast((1 - y_true) * y_pred, 'float32'))
    fn = K.sum(K.cast(y_true * (1 - y_pred), 'float32'))

    precision = tp / (tp + fp + K.epsilon())  # Avoid division by zero
    recall = tp / (tp + fn + K.epsilon())

    return 2 * (precision * recall) / (precision + recall + K.epsilon())
