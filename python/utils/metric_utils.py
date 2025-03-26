from tensorflow import keras

# Custom F1-score function
def f1_score(y_true, y_pred):
    precision = keras.metrics.Precision()(y_true, y_pred)
    recall = keras.metrics.Recall()(y_true, y_pred)
    return 2 * (precision * recall) / (precision + recall + keras.backend.epsilon())  # Avoid division by zero
