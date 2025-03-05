from ds.dataset import DataSet
from utils.csv_utils import write_csv_meta
from utils.json_utils import append_empty_mapping_to_config, get_post_class_mapping
from utils.wav_utils import get_wav_data_length
import constants as C
import pandas as pd
import os
import zipfile
import requests
import re

class BDLib2(DataSet):
    """BDLib2 Dataset"""

    def __init__(self):
        # Determine the dataset path
        self.ds_abs_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "datasets", "bdlib2"))

        # Ensure the directory exists before calling super()
        os.makedirs(self.ds_abs_path, exist_ok=True)

        super().__init__("bdlib2")

    def download(self):
        zip_url = "https://research.playcompass.com/files/BDLib-2.zip"
        zip_path = os.path.join(self.ds_abs_path, "BDLib2.zip")
        extract_path = os.path.join(self.ds_abs_path, "BDLib2")

        # Ensure the directory exists before downloading the file
        os.makedirs(self.ds_abs_path, exist_ok=True)

        # Download the ZIP file if it does not exist
        if not os.path.exists(zip_path):
            print(f"📥 Downloading BDLib2 dataset from {zip_url}...")
            response = requests.get(zip_url, stream=True)
            with open(zip_path, "wb") as f:
                for chunk in response.iter_content(chunk_size=8192):
                    f.write(chunk)

        # Download the ZIP file if it does not exist
        if not os.path.exists(extract_path):
            print("📂 Extracting BDLib2 dataset...")
            with zipfile.ZipFile(zip_path, "r") as zip_ref:
                zip_ref.extractall(self.ds_abs_path)

        return extract_path 

    def init_class_names(self):
        """Create a class list from the directory"""
        if not os.path.exists(self.ds_abs_path):
            self.download()  
        columns = ["idx", "filename", "filepath", "category"]
        original_ds_df = pd.DataFrame(columns=columns)
        index = 0

        valid_classes = set(get_post_class_mapping(self.key).keys())

        for fold in ["fold-1", "fold-2", "fold-3"]:
            sub_f_path = os.path.join(self.ds_abs_path, fold)
            if not os.path.exists(sub_f_path):
                continue  # Skip if the directory does not exist

            for f in os.listdir(sub_f_path):
                if f.endswith(".wav"):
                    file_name = f
                    file_path = os.path.join(fold, f)

                    # Find the class name in the filename
                    match = re.match(r"([a-zA-Z_]+)\d+\.wav", file_name)
                    if match:
                        category = match.group(1)  # Extract class name from the filename
                    else:
                        category = "unknown"

                    # Check if the category is valid
                    if category not in valid_classes:
                        continue  # Skip if it's not in config.json

                    original_ds_df.loc[len(original_ds_df)] = [index, file_name, file_path, category]
                    index += 1

        # Create meta `.csv` file
        path = write_csv_meta(original_ds_df, self.key + ".original")
        self.meta_sub_path = path

    def filter_by_class(self):
        """Filter data by class mapping"""
        df = pd.read_csv(self.meta_sub_path)
        class_map = get_post_class_mapping(self.key)
        df_filtered = df[df["category"].isin(class_map)]
        df = df_filtered

        self.df[C.DF_NAME_COL] = df["filename"]
        self.df[C.DF_PATH_COL] = df["filepath"].apply(lambda fp: os.path.join(self.ds_abs_path, fp))
        self.df[C.DF_LENGTH_COL] = self.df[C.DF_PATH_COL].apply(get_wav_data_length)
        self.df[C.DF_CLASS_ID_COL] = df["category"].apply(lambda original_classname: class_map[original_classname][C.CLASS_ID])
        self.df[C.DF_CLASS_NAME_COL] = df["category"].apply(lambda original_classname: class_map[original_classname][C.CLASS_NAME])
        self.df[C.DF_SUB_DS_NAME_COL] = self.name
        self.df[C.DF_SUB_DS_ID_COL] = df.index

    def normalize(self):
        """Not implemented yet"""
        pass

    def create_meta(self):
        """Create the filtered meta file"""
        df = pd.read_csv(self.meta_sub_path)
        path = write_csv_meta(self.df, self.key + ".filtered")
        _ = write_csv_meta(df, self.key + ".original")
        self.filtered_meta_path = path

    def get_filtered_meta_path(self):
        return self.filtered_meta_path

    def move_files(self):
        """No need to move files"""
        pass
