import kagglehub
import pandas as pd
from dataset import DataSet



class ESC50(DataSet):
    def __init__(self):
        super().__init__("esc50")

    def download(self):
        print(f"kg path: {self.kaggle_path}")
        ds_abs_path = kagglehub.dataset_download(self.kaggle_path)
        return ds_abs_path
    
    def init_class_names(self):
        df = pd.read_csv(self.meta_sub_path)
        self.class_names = df["category"].unique()

    def filter_by_class(self):
        pass

    def normalize(self):
        pass

    def create_meta(self):
        pass

    def move_files(self):
        pass
    


def main():
    ds = ESC50()
    print(ds)
    
if __name__ == "__main__":
    main()