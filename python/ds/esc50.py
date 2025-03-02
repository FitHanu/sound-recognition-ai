import kagglehub
import os
import pandas as pd
import constants as C
from dataset import DataSet
from json_utils import get_post_class_mapping
from wav_utils import get_wav_data_length
from csv_utils import write_csv_meta
# from concurrent.futures import ProcessPoolExecutor



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
        # Load and filter the original meta file
        df = pd.read_csv(self.meta_sub_path)
        class_map = get_post_class_mapping(self.key)
        df_filtered = df[df["category"].isin(class_map.keys())]
        # print(f"df shape before filter: {df.shape}")
        df = df_filtered
        # print(f"df shape after filter: {df_filtered.shape}")
        # print(df.head(5))
        # print(df.tail(5))
        
        # Map filtered dataframe to self.df with the right schema
        # self.df[C.DF_ID_COL] = range(len(df))
        self.df[C.DF_NAME_COL] = df["filename"]
        self.df[C.DF_PATH_COL] = df["filename"].apply(lambda filename: os.path.join(self.data_sub_path, filename))
        self.df[C.DF_LENGTH_COL] = self.df[C.DF_PATH_COL].apply(get_wav_data_length)
        self.df[C.DF_CLASS_ID_COL] = df["category"].apply(lambda original_classname: class_map[original_classname][C.CLASS_ID])
        self.df[C.DF_CLASS_NAME_COL] = df["category"].apply(lambda original_classname: class_map[original_classname][C.CLASS_NAME])
        self.df[C.DF_SUB_DS_NAME_COL] = self.name
        self.df[C.DF_SUB_DS_ID_COL] = df.index

    def normalize(self):
        """
        Currently not implemented
        """
        pass

    def create_meta(self):
        df = pd.read_csv(self.meta_sub_path)
        write_csv_meta(self.df, self.key + ".filtered")
        write_csv_meta(df, self.key + ".original")

    def move_files(self):
        """
        Currently no need to move files, planned datasets would not be stotage comsuming after extracted  
        """
        pass
    


def main():
    ds = ESC50()
    df = pd.read_csv(ds.meta_sub_path)
    print(ds.df) # Columns: [id, file_name, file_path, length, class_id, class_name, sub_ds_name, sub_ds_id]
    print(df.columns) # Index(['filename', 'fold', 'target', 'category', 'esc10', 'src_file', 'take'], dtype='object')
    
    ds.filter_by_class()

    
if __name__ == "__main__":
    main()