import kagglehub
import os
import pandas as pd
import constants as C
from dataset import DataSet
from utils.json_utils import get_post_class_mapping
from utils.wav_utils import get_wav_data_length
from utils.csv_utils import write_csv_meta
from logging_cfg import get_logger
l = get_logger(__name__)

class UrbanSound8K(DataSet):
    def __init__(self):
        super().__init__("us8k")

    def download(self):
        """Download dataset using Kaggle API."""
        l.info(f"📥 Downloading dataset from: {self.kaggle_path}")
        ds_abs_path = kagglehub.dataset_download(self.kaggle_path)
        return ds_abs_path

    def init_class_names(self):
        """Initialize class names from metadata file."""
        df = pd.read_csv(self.meta_sub_path)
        self.class_names = df["class"].unique()

    def filter_by_class(self):
        """Filter dataset based on predefined class mappings."""
        df = pd.read_csv(self.meta_sub_path)
        class_map = get_post_class_mapping(self.key)
        df_filtered = df[df["class"].isin(class_map.keys())]

        # Mapping columns to match system format
        self.df = pd.DataFrame()
        self.df[C.DF_NAME_COL] = df_filtered["slice_file_name"]
        self.df[C.DF_PATH_COL] = df_filtered.apply(
            lambda row: os.path.abspath(
                os.path.join(self.data_sub_path, f"fold{row['fold']}", row["slice_file_name"])
            ), axis=1
        )

        # Check if all files exist before proceeding
        # missing_files = self.df[~self.df[C.DF_PATH_COL].apply(os.path.isfile)]
        # if not missing_files.empty:
        #     l.warning("⚠️ Warning: Some files do not exist!")
        #     l.warning(f"Theres are the missing files: {missing_files.shape[0]}")

        # Apply `get_wav_data_length()` only to existing files
        self.df[C.DF_LENGTH_COL] = self.df[C.DF_PATH_COL].apply(
            lambda x: get_wav_data_length(x) if os.path.isfile(x) else None
        )

        self.df[C.DF_CLASS_ID_COL] = df_filtered["class"].apply(lambda original_classname: class_map[original_classname][C.CLASS_ID])
        self.df[C.DF_CLASS_NAME_COL] = df_filtered["class"].apply(lambda original_classname: class_map[original_classname][C.CLASS_NAME])
        self.df[C.DF_SUB_DS_NAME_COL] = self.name
        self.df[C.DF_SUB_DS_ID_COL] = df_filtered.index

    def normalize(self):
        """Normalization not implemented yet."""
        pass

    def create_meta(self):
        """Create metadata for the filtered dataset."""
        df = pd.read_csv(self.meta_sub_path)
        path = write_csv_meta(self.df, self.key + ".filtered")
        _ = write_csv_meta(df, self.key + ".original")
        self.filtered_meta_path = path

    def get_filtered_meta_path(self):
        """Return path to the filtered metadata file."""
        return self.filtered_meta_path

    def move_files(self):
        """No need to move files, dataset structure is already organized."""
        pass


# def main():
#     ds = UrbanSound8K()
#     df = pd.read_csv(ds.meta_sub_path)
#     print(ds.df)  # Expected columns: [id, file_name, file_path, length, class_id, class_name, sub_ds_name, sub_ds_id]
#     print(df.columns)  # Expected: ['slice_file_name', 'fold', 'classID', 'class', ...]

#     ds.filter_by_class()


# if __name__ == "__main__":
#     main()
