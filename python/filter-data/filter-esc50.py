import kagglehub
import os
import sys
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))
from utils.json_utils import get_dataset_info

KEY = "esc50"

info = get_dataset_info(KEY)
print(info)

# Load the dataset
path = kagglehub.dataset_download(info.kaggle_path)
print(path)