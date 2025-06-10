package org.fit.sra.state;

import java.util.ArrayList;
import java.util.List;
import org.tensorflow.lite.support.label.Category;

public class AppStateManager {

  private static AppStateManager instance;
  private boolean isRecording = false;

  private List<Category> recognitionCategories;
  private List<RecordingStateListener> isRecordingListeners = new ArrayList<>();
  private List<CategoryStateListener>  categoriesListeners  = new ArrayList<>();

  public static AppStateManager getInstance() {
    if (instance == null) {
      instance = new AppStateManager();
    }
    return instance;
  }

  public interface RecordingStateListener {
    void onRecordingStateChanged(boolean isRecording);
  }

  public interface CategoryStateListener {
    void onCategoryStateChanged(List<Category> categories);
  }


  public void addIsRecordListener(RecordingStateListener listener) {
    isRecordingListeners.add(listener);
  }

  public void addCategoriesListener(CategoryStateListener listener) {
    categoriesListeners.add(listener);
  }



  public void removeListener(RecordingStateListener listener) {
    isRecordingListeners.remove(listener);
  }

  public void setRecording(boolean recording) {
    this.isRecording = recording;
    notifyListeners();
  }

  public void toggleRecoding() {
    this.isRecording = !this.isRecording;
    notifyListeners();
  }

  public boolean isRecording() {
    return isRecording;
  }

  public List<Category> getRecognitionCategories() {
    return recognitionCategories;
  }

  public void setRecognitionCategories(
      List<Category> recognitionCategories) {
    this.recognitionCategories = recognitionCategories;
    notifyCategoryListeners();
  }


  private void notifyListeners() {
    for (RecordingStateListener listener : isRecordingListeners) {
      listener.onRecordingStateChanged(isRecording);
    }

  }

  private void notifyCategoryListeners() {

    for (CategoryStateListener listener : categoriesListeners) {
      listener.onCategoryStateChanged(this.recognitionCategories);
    }
  }
}
