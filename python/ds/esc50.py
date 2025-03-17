import os
import pandas as pd
import constants as C
from dataset import DataSet
from utils.json_utils import get_post_class_mapping
from utils.wav_utils import get_wav_data_length
from utils.csv_utils import write_csv_meta
# from concurrent.futures import ProcessPoolExecutor



class ESC50(DataSet):
    def __init__(self):
        super().__init__("esc50")

    # def download(self):
    #     ds_abs_path = kagglehub.dataset_download(self.kaggle_path)
        
    #     meta_rel_path = self.json_meta["csv_meta_path"]
    #     meta_rel_path = os.path.join(*meta_rel_path)
        
    #     data_rel_path = self.json_meta["data_path"]
    #     data_rel_path = os.path.join(*data_rel_path)
        
    #     ds_paths = DataSetPaths(ds_abs_path, meta_rel_path, data_rel_path)
    #     return ds_abs_path

    def init_class_names(self):
        df = pd.read_csv(self.get_paths().get_meta_path())
        self.class_names = df["category"].unique()

    def filter_by_class(self) -> pd.DataFrame:
        meta_path = self.get_paths().get_meta_path()
        audio_path = self.get_paths().get_data_path()
        # Load and filter the original meta file
        df = pd.read_csv(meta_path)
        class_map = get_post_class_mapping(self.key)
        df_filtered = df[df["category"].isin(class_map.keys())]
        df = df_filtered
        
        from ds.dataset import PD_SCHEMA
        final_df = pd.DataFrame(columns=PD_SCHEMA.keys()).astype(PD_SCHEMA)
        
        # Map filtered dataframe to self.df with the right schema
        # self.df[C.DF_ID_COL] = range(len(df))
        final_df[C.DF_NAME_COL] = df["filename"]
        final_df[C.DF_PATH_COL] = df["filename"].apply(lambda filename: os.path.join(audio_path, filename))
        final_df[C.DF_LENGTH_COL] = final_df[C.DF_PATH_COL].apply(get_wav_data_length)
        final_df[C.DF_CLASS_ID_COL] = df["category"].apply(lambda original_classname: class_map[original_classname][C.CLASS_ID])
        final_df[C.DF_CLASS_NAME_COL] = df["category"].apply(lambda original_classname: class_map[original_classname][C.CLASS_NAME])
        final_df[C.DF_SUB_DS_NAME_COL] = self.name
        final_df[C.DF_SUB_DS_ID_COL] = df.index
        
        return final_df

    def normalize(self):
        """
        Currently not implemented
        """
        pass

    def create_meta(self):
        df = pd.read_csv(self.get_paths().get_meta_path())
        path = write_csv_meta(self.df, self.key + ".filtered")
        _ = write_csv_meta(df, self.key + ".original")
        return path
    
    def get_filtered_meta_path(self):
        return self.filtered_meta_path

    # def move_files(self):
    #     """
    #     Currently no need to move files, planned datasets would not be stotage comsuming after extracted  
    #     """
    #     pass


def main():
    ds = ESC50()
    ds.hell_yeah()
    df = pd.read_csv(ds.get_paths().get_meta_path())
    print(ds.df) # Columns: [id, file_name, file_path, length, class_id, class_name, sub_ds_name, sub_ds_id]
    print(df.columns) # Index(['filename', 'fold', 'target', 'category', 'esc10', 'src_file', 'take'], dtype='object')

    
if __name__ == "__main__":
    main()