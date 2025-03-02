import kagglehub
import os
import sys
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))
from utils.json_utils import get_dataset_info, append_empty_mapping_to_config 
import pandas as pd

JSON_KEY = "esc50"
DATA_SET_ABS_PATH = ""
METADATA_PATH = os.path.join("ESC-50-master", "ESC-50-master", "meta", "esc50.csv")
AUDIO_SUB_PATH = os.path.join("ESC-50-master", "ESC-50-master", "audio")
LABEL_COL = "category"

info = get_dataset_info(JSON_KEY)
print(info)
DATA_SET_ABS_PATH = kagglehub.dataset_download(info["kaggle_path"])

# List the files in the dataset directory
audio_files = os.listdir(os.path.join(DATA_SET_ABS_PATH, AUDIO_SUB_PATH))[:10]
print(audio_files)

# Load the CSV file
csv_path = os.path.join(DATA_SET_ABS_PATH, METADATA_PATH)
df = pd.read_csv(csv_path)


# Handle appending to config.json
append_empty_mapping_to_config(
    df=df,
    ds=info,
    label_col=LABEL_COL,
    overwrite=False
)
