from ds.dataset import DataSet
from utils.csv_utils import write_csv_meta
from utils.json_utils import append_empty_mapping_to_config, get_post_class_mapping
from utils.wav_utils import get_wav_data_length
import constants as C
import kagglehub
import pandas as pd
import zipfile
import os
import gdown
from file_utils import extract
from platformdirs import user_cache_dir

class ThaoLp(DataSet):
    """_summary_
        Self colected dataset
    """
    def __init__(self):
        key = "thaoLp"
        super().__init__(key)

    def download(self):
        pass

    def init_class_names(self):
        """_summary_
        This one doesn;t have meta file so we:
        1. load folder names as class names
        2. Make a meta file in `.csv`
        3. Assign created `.csv` filepath to `self.meta_sub_path`
        """
        
        # # Load folder name as class names
        # folders = [f.name for f in os.scandir(self.ds_abs_path) if f.is_dir()]
        # df = pd.DataFrame(folders, columns=["category"])
        # self.class_names = df["category"].unique()

        # columns = ["idx", "filename", "filepath", "category"]        
        # original_ds_df = pd.DataFrame(columns=columns)
        # index = 0;
        # for category in folders:
        #     sub_f_path = os.path.join(self.ds_abs_path, category)
        #     sub_f_files = [f.name for f in os.scandir(sub_f_path) if f.is_file() and f.name.endswith('.wav')]
        #     for f in sub_f_files:
        #         file_name = f
        #         file_path = os.path.join(category, f)
        #         original_ds_df.loc[len(original_ds_df)] = [index, file_name, file_path, category]
        #         index += 1
                
        # # Init original dataset .csv meta file
        # path = write_csv_meta(original_ds_df, self.key + ".original")
        # self.meta_sub_path = path
        

    def filter_by_class(self):
        pass

    def normalize(self):
        """
        Currently not implemented
        """
        pass

    def create_meta(self):
        pass
    
    def get_filtered_meta_path(self):
        pass

    def move_files(self):
        """
        Currently no need to move files, planned datasets would not be stotage comsuming after extracted  
        """
        pass
    
def main():
    ds = ThaoLp() 
    # append_empty_mapping_to_config(ds, overwrite=False)
    ds.download()

if __name__ == "__main__":
    main()