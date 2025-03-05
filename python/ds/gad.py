from ds.dataset import DataSet
from utils.csv_utils import write_csv_meta
from utils.json_utils import append_empty_mapping_to_config, get_post_class_mapping
from utils.wav_utils import get_wav_data_length
import constants as C
import kagglehub
import pandas as pd
import os

class GAD(DataSet):
    """_summary_
        Gunshot Audio Dataset
    """
    def __init__(self):
        super().__init__("gad")

    def download(self):
        ds_abs_path = kagglehub.dataset_download(self.kaggle_path)
        return ds_abs_path
    
    def init_class_names(self):
        """_summary_
        This one doesn;t have meta file so we:
        1. load folder names as class names
        2. Make a meta file in `.csv`
        3. Assign created `.csv` filepath to `self.meta_sub_path`
        """
        
        # Load folder name as class names
        folders = [f.name for f in os.scandir(self.ds_abs_path) if f.is_dir()]
        df = pd.DataFrame(folders, columns=["category"])
        self.class_names = df["category"].unique()

        columns = ["idx", "filename", "filepath" "category"]        
        original_ds_df = pd.DataFrame(columns=columns)
        index = 0;
        for category in folders:
            sub_f_path = os.path.join(self.ds_abs_path, category)
            sub_f_files = [f.name for f in os.scandir(sub_f_path) if f.is_file() and f.name.endswith('.wav')]
            for f in sub_f_files:
                file_name = f
                file_path = os.path.join(category, f)
                original_ds_df.loc[len(original_ds_df)] = [index, file_name, file_path, category]
                index += 1
                
        # Init original dataset .csv meta file
        path = write_csv_meta(original_ds_df, self.key + ".original")
        self.meta_sub_path = path
        

    def filter_by_class(self):
        """_summary_
        """
        # # Load and filter the original meta file
        df = pd.read_csv(self.meta_sub_path)
        # Get allowed classes and their mapping
        class_map = get_post_class_mapping(self.key)
        # Eliminate classes not in class_map
        df_filtered = df[df["category"].isin(class_map.keys())]
        df = df_filtered
        print(df.head(10))
        
        # # Map filtered dataframe to self.df with the right schema
        # # self.df[C.DF_ID_COL] = range(len(df))
        self.df[C.DF_NAME_COL] = df["filename"]
        self.df[C.DF_PATH_COL] = df["filepath"].apply(lambda fp: os.path.join(self.ds_abs_path, fp))
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
        path = write_csv_meta(self.df, self.key + ".filtered")
        _ = write_csv_meta(df, self.key + ".original")
        self.filtered_meta_path = path
    
    def get_filtered_meta_path(self):
        return self.filtered_meta_path

    def move_files(self):
        """
        Currently no need to move files, planned datasets would not be stotage comsuming after extracted  
        """
        pass
    
def main():
    ds = GAD()
    append_empty_mapping_to_config(ds, overwrite=False)
    ds.hell_yeah()

if __name__ == "__main__":
    main()