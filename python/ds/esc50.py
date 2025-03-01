from ds.dataset import Dataset
import kagglehub

class ESC50(Dataset):
    def __init__(self, key: str):
        super().__init__(key)

    def download(self):
        ds_abs_path = kagglehub.dataset_download(self.kaggle_path)
        return ds_abs_path
    
    def init_class_names(self):
        self.class_names = ["dog", "rooster", "pig", "cow", "frog", "cat", "hen", "insects", "sheep", "crow"]

    def filter_by_class(self):
        pass

    def normalize(self):
        pass

    def create_meta(self):
        pass

    def move_files(self):
        pass