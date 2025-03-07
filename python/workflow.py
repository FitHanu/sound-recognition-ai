"""
The project main function
"""
import os
import pandas as pd
import constants as C
from constants import PROJECT_ROOT
from ds.dataset import PD_SCHEMA
from utils.json_utils import init_default_class_name, append_empty_mapping_to_config
from utils.file_utils import init_class_folds
from utils.csv_utils import read_csv_as_dataframe, write_csv_meta
from utils.dframe_utils import plot_classname_distribution
from ds.esc50 import ESC50
from ds.us8k import UrbanSound8K
from ds.bdlib2 import BDLib2
from ds.gad import GAD

from logging_cfg import get_logger
l = get_logger(__name__)

datasets_registry = [
    ESC50(),
    GAD(),
    UrbanSound8K(),
    BDLib2(),
]

def main():
    # Main procedure
    DATASET_PATH_FILTERED = os.path.join(PROJECT_ROOT, "dataset")
    l.info(f"Creating empty dataset directory to {DATASET_PATH_FILTERED}")
    os.makedirs(DATASET_PATH_FILTERED, exist_ok=True)
    init_default_class_name()
    init_class_folds(DATASET_PATH_FILTERED)
    main_df = pd.DataFrame(columns=PD_SCHEMA.keys()).astype(PD_SCHEMA)

    # Data filtering & class mapping
    for ds in datasets_registry:
        l.info(f"Filtering & mapping class names for {ds.key}")
        append_empty_mapping_to_config(ds, overwrite=False)
        ds.hell_yeah()
        df = read_csv_as_dataframe(ds.get_filtered_meta_path())
        l.info(f"\"{ds.key}\" shape after filter: {df.shape}")
        main_df = pd.concat([main_df, df], ignore_index=True)

    l.info(f"Done filtering & mapping class names for all datasets")
    l.info(f"Main dataframe shape: {main_df.shape}")
    print(main_df.shape)
    from constants import FULL_META_CSV
    l.info(f"Writing metafile {FULL_META_CSV}")
    write_csv_meta(main_df, "merged")
    missing_files = main_df[main_df[C.DF_PATH_COL].apply(os.path.isfile)]
    l.info(f"Missing files: {missing_files.shape[0]}")
    missing_files.to_csv(C.PY_PROJECT_ROOT + os.path.sep + "missing_files.csv", index=False)
    
    plot_classname_distribution(main_df)
    
    #TODO: construct dataset folds for each class_name, normalize file numbers each fold
    #TODO: construct split train, dev, test (80, 10, 10) for each class_name(fold)
    
    

    
if __name__ == "__main__":
    main()