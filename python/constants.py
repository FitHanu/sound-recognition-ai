import os
from pathlib import Path
import site


# default meta column config
DF_ID_COL = "id"
DF_NAME_COL = "file_name"
DF_PATH_COL = "file_path"
DF_TEMP_NEW_NAME_COL = "temp_new_name"
DF_LENGTH_COL = "length"
DF_CLASS_ID_COL = "class_id"
DF_CLASS_NAME_COL = "class_name"
DF_SUB_DS_NAME_COL = "sub_ds_name"
DF_SUB_DS_ID_COL = "sub_ds_index"

# Constant when working with class mapping
CLASS_ID = 0
CLASS_NAME = 1


# Config directory
PROJECT_ROOT = Path(__file__).resolve().parent.parent
PY_PROJECT_ROOT = os.path.join(PROJECT_ROOT, "python")
SITE_PKG_PATH = site.getsitepackages()[0]
CONFIG_JSON = os.path.join(PY_PROJECT_ROOT, "config.json")
DATASET_JSON = os.path.join(PY_PROJECT_ROOT, "datasets.json")
META_PATH = os.path.join(PY_PROJECT_ROOT, "ds", "meta")
FULL_META_CSV = os.path.join(PY_PROJECT_ROOT, "ds", "meta", "merged.csv")
CLASSES_CSV = os.path.join(PY_PROJECT_ROOT, "classes.csv")
FINAL_DATASET_PATH = os.path.join(PROJECT_ROOT, "dataset")
LOG_PATH = os.path.join(PY_PROJECT_ROOT, "logs")