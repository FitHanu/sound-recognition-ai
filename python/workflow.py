"""
The project main function
"""
import os
from setup import PROJECT_ROOT
from utils.json_utils import init_default_class_name, append_empty_mapping_to_config
from utils.file_utils import init_class_folds
from ds.esc50 import ESC50

datasets_registry = [
    ESC50(),
]

def main():
    # Main procedure
    DATASET_PATH_FILTERED = os.path.join(PROJECT_ROOT, "dataset")
    init_default_class_name()
    init_class_folds(DATASET_PATH_FILTERED)

    for ds in datasets_registry:
        append_empty_mapping_to_config(ds, overwrite=False)
        ds.hell_yeah()
    
    
if __name__ == "__main__":
    main()