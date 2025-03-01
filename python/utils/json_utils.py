import json
import os
import pandas as pd
from pathlib import Path
from ds.dataset import DataSet
import logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
l = logging.getLogger(__name__)

cwd = Path(__file__).resolve().parent.parent
datasets_file_path = os.path.join(cwd, "datasets.json")
config_file_path = os.path.join(cwd, "config.json")

def get_dataset_info(key: str):
    with open(datasets_file_path) as f:
        data = json.load(f)
        try:
            obj = data[key]
        except KeyError:
            msg = f"No dataset found in {datasets_file_path} for key: {key}"
            raise KeyError(msg)
        return {
            "key": key,
            "name": obj["name"],
            "format": obj["format"],
            "kaggle_path": obj["kaggle_path"],
            "url": obj["url"],
            "csv_meta_path": obj["csv_meta_path"],
            "data_path": obj["data_path"]
        }
        
def append_empty_mapping_to_config(ds: DataSet,
                                overwrite: bool = False):
    l.info(f"Writing config to {config_file_path}, Overiding: {overwrite}")
    parent_property = "class_mapping"
    ds_key = ds["key"]

    def is_key_empty():
        """
        True if
        1. key is empty in config.json
        2. content in key object is empty
        """
        if parent_property not in data:
            return True
        return not bool(data[parent_property].get(ds_key))

    with open(config_file_path, "r") as f:
        data = json.load(f)
        if parent_property not in data:
            data[parent_property] = {}
        else:
            if not is_key_empty() and overwrite == False:
                l.warning(f"Overwrite is set to off but {ds_key} is already exists. Skipping ...")
                return
        data[parent_property][ds_key] = {}
        l.info(f"Appending empty classes to config.{parent_property}.{ds_key} ...")
        """
        Append empty class1: "",
                     class2: "",
                     ...
        for dataset class mapping manually
        """
        column = ds.class_names
        for label in column:
            if label not in data[parent_property][ds_key]:
                data[parent_property][ds_key][label] = 0
    with open(config_file_path, "w") as file:
        json.dump(data, file, indent=2)
    l.info(f"Appended {column.size} empty classes to config.{parent_property}.{ds_key}.")
        

def init_default_class_name():
    parent_property = "class_mapping"
    default_cn_property = "default"
    df = pd.read_csv(DataSet.DEFAULT_CLASSNAME_PATH)
    with open(config_file_path, "r") as f:
        data = json.load(f)
        data[parent_property][default_cn_property] = {}
        for _, row in df.iterrows():
            data[parent_property][default_cn_property][row["class_name"]] = row["id"]
    with open(config_file_path, "w") as file:
        json.dump(data, file, indent=2)
    l.info(f"default class names written to config.{parent_property}.{default_cn_property}")