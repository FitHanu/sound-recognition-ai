"""
The project main function
"""
import os
import pandas as pd
from setup import PROJECT_ROOT
from dataset import PD_SCHEMA
from utils.json_utils import init_default_class_name, append_empty_mapping_to_config
from utils.file_utils import init_class_folds
from utils.csv_utils import read_csv_as_dataframe, write_csv_meta
from utils.dframe_utils import plot_classname_distribution
from ds.esc50 import ESC50
from ds.us8k import UrbanSound8K
from ds.bdlib2 import BDLib2
from ds.gad import GAD

datasets_registry = [
    # TODO: Add more datasets instance here
    ESC50(),
    UrbanSound8K(),
    BDLib2()
    GAD(),
]

def main():
    # Main procedure
    DATASET_PATH_FILTERED = os.path.join(PROJECT_ROOT, "dataset")
    os.makedirs(DATASET_PATH_FILTERED, exist_ok=True)

    init_default_class_name()
    init_class_folds(DATASET_PATH_FILTERED)
    main_df = pd.DataFrame(columns=PD_SCHEMA.keys()).astype(PD_SCHEMA)

    # Data filtering & class mapping
    for ds in datasets_registry:
        append_empty_mapping_to_config(ds, overwrite=False)
        ds.hell_yeah()
        df = read_csv_as_dataframe(ds.get_filtered_meta_path())
        print(df.shape)
        main_df = pd.concat([main_df, df], ignore_index=True)

    print(main_df.shape)
    write_csv_meta(main_df, "merged")
    plot_classname_distribution(main_df)
    
    #TODO: construct dataset folds for each class_name, normalize file numbers each fold
    #TODO: construct split train, dev, test (80, 10, 10) for each class_name(fold)
    
    

    
if __name__ == "__main__":
    main()