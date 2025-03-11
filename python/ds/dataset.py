import abc
import os
import pandas as pd
import constants as C
from pathlib import Path
from typing import final

"""
Default Schema for each dataset meta file/ pandas DataFrame
"""
PD_SCHEMA = {
    # Unique ID for each audio file
    # C.DF_ID_COL: "int64",
    # File name
    C.DF_NAME_COL: "string",
    # Audio file path
    C.DF_PATH_COL: "string",
    # Audio file length (must be in milliseconds)
    C.DF_LENGTH_COL: "int64",
    # Class Name ID
    C.DF_CLASS_ID_COL: "int16",
    # Class Name (string)
    C.DF_CLASS_NAME_COL: "string",
    # Original dataset name
    C.DF_SUB_DS_NAME_COL: "string",
    # ID in the original dataset
    C.DF_SUB_DS_ID_COL: "string", 
}

class DataSet(abc.ABC):
    """
    Abstract class representing a dataset
    ** ABS: could not be instantiated **
    """
    
    # Default class names
    THIS_SCRIPT_PATH = Path(__file__).resolve().parent.parent
    DEFAULT_CLASSNAME_PATH = os.path.join(THIS_SCRIPT_PATH, "classes.csv")
    DEFAULT_CLASSNAMES_DF = pd.read_csv(DEFAULT_CLASSNAME_PATH)
    
    """
    "FILTERED_DATASET_PATH": Our full dataset (filtered) path
    
    After filter, each sub dataset must have:
    + A meta file in with "META_PATH"
    + Data inside "FILTERED_DATASET_PATH"
    """
    PROJECT_PATH = THIS_SCRIPT_PATH.parent
    FILTERED_DATASET_PATH = os.path.join(PROJECT_PATH, "dataset", "audio")
    META_PATH = os.path.join(PROJECT_PATH, "dataset", "meta")
    
    
    def __init__(self, key: str):

        from utils.json_utils import get_dataset_info
        info = get_dataset_info(key)
        
        self.key = info["key"]
        self.name = info["name"]
        self.format = info["format"]
        self.kaggle_path = info["kaggle_path"]
        self.url = info["url"]
        ds_abs_path = self.download()
        self.ds_abs_path = ds_abs_path
        self.meta_sub_path = os.path.join(ds_abs_path, *info["csv_meta_path"])
        self.data_sub_path = os.path.join(ds_abs_path, *info["data_path"])
        self.init_class_names()
        # self.df need to be reinitialized/ poured with data in the child class filter_by_class function
        self.df = pd.DataFrame(columns=PD_SCHEMA.keys()).astype(PD_SCHEMA)
        
    def get_key(self):
        return self.key

    @abc.abstractmethod  
    def get_filtered_meta_path(self):
        """
        After filter & create meta
        """

    def __str__(self):
        return (f"DataSet(\n"
                f"  key={self.key},\n"
                f"  name={self.name},\n"
                f"  format={self.format},\n"
                f"  abs_path={self.ds_abs_path}\n"
                f"  meta_sub_path={self.meta_sub_path}\n"
                f"  data_sub_path={self.data_sub_path}\n"
                f"  df_shape={self.df.shape}\n"
                f"  df_types={self.df.dtypes}\n"
                f")")
        
    @abc.abstractmethod
    def init_class_names(self):
        """
        - Initialize the class names for the dataset
        Type of self.class_names should be List[str]
        """
        self.class_names = []
        pass
    
    @abc.abstractmethod
    def download(self):
        """
        Override this method to download the dataset
            - Download method could either be from Kaggle API or from a direct link depending on the self.format
        Should return the absolute path of the dataset in the working file system
        """
        path = ""
        return path
    
    @abc.abstractmethod
    def filter_by_class(self):
        """
        - Overide this method to perform following operations:
        + Filter the dataset by mapped class in the config.json
        + Should return a pd DataFrame contain audio file paths with their 
        corresponding system default class name
        """
        
    
    @abc.abstractmethod
    def normalize(self):
        """
        - Normalize the dataset by renaming the audio files to the default class name
        """
        pass
    
    @abc.abstractmethod
    def create_meta(self):
        """
        - Create a new meta file for the dataset with "PD_SCHEMA" defined above
        """
        pass
    
    @abc.abstractmethod
    def move_files(self):
        """
        - Move files from the dataset to the target directory
        - The target directory is defined in the config.json
        """
        pass
    
    @final
    def hell_yeah(self):
        """
        Main flow/ life cycle :v of the dataset processing
        """
        
        # self.download() # Dataset already downloaded in __init__
        self.filter_by_class()
        self.normalize()
        self.create_meta()
        self.move_files()