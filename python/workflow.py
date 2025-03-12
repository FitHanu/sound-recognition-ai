"""
The project entry script
"""
import os
import pandas as pd
import constants as C
from constants import PROJECT_ROOT
from ds.dataset import PD_SCHEMA
from utils.json_utils import init_default_class_name, append_empty_mapping_to_config
from utils.file_utils import init_class_folds, get_filename_without_extension
from utils.csv_utils import read_csv_as_dataframe, write_csv_meta
from utils.dframe_utils import plot_classname_distribution, copy_update_dataset_file
from partition.split_tdt import split_tdt, init_cfg
from ds.esc50 import ESC50
from ds.us8k import UrbanSound8K
from ds.bdlib2 import BDLib2
from ds.gad import GAD

from logging_cfg import get_logger
l = get_logger(__name__)

def workflow():
    """
    Main procedure
    """
    datasets_registry = [
        ESC50(),
        GAD(),
        UrbanSound8K()
        # BDLib2(),
    ]
    
    # Init paths, Default class names
    DATASET_PATH_FILTERED = os.path.join(PROJECT_ROOT, "dataset")
    l.info(f"Creating empty dataset directory to {DATASET_PATH_FILTERED}")
    os.makedirs(DATASET_PATH_FILTERED, exist_ok=True)
    init_default_class_name()
    # init_class_folds(DATASET_PATH_FILTERED)
    
    # Init main dataframe
    main_df = pd.DataFrame(columns=PD_SCHEMA.keys()).astype(PD_SCHEMA)

    # Process each dataset
    for ds in datasets_registry:
        # Add empty mapping to config (initial)
        l.info(f"Filtering & mapping class names for {ds.key}")
        append_empty_mapping_to_config(ds, overwrite=False)
        
        # Call ds life cycle methods
        ds.hell_yeah()
        l.info(f"Dataset: \"{ds.name}\" saved to {ds.ds_abs_path}")
        # Read filtered metafile
        df = read_csv_as_dataframe(ds.get_filtered_meta_path())
        
        # Append to main dataframe
        main_df = pd.concat([main_df, df], ignore_index=True)
        l.info(f"main_df shape after filter: {main_df.shape}")

    l.info(f"Done filtering & mapping class names for all datasets")
    l.info(f"Main dataframe shape: {main_df.shape}")

    # Write main dataframe to csv
    from constants import FULL_META_CSV
    l.info(f"Writing metafile {FULL_META_CSV}")
    write_csv_meta(main_df, "merged")
    
    # Count missing files after filtering
    missing_files = main_df[main_df[C.DF_PATH_COL].apply(os.path.isfile)]
    if (missing_files.shape[0] > 0):
        l.warning(f"Missing files: {missing_files.shape[0]}")
        missing_files.to_csv(C.PY_PROJECT_ROOT + os.path.sep + "missing_files.csv", index=False)
    
    # Plot class name distribution
    # plot_classname_distribution(main_df)
    
    # Get split config
    cfg = init_cfg()
    
    l.info(f"Spliting with cfg {cfg.__str__()}")
    
    # Split
    aug_k_df = split_tdt(main_df, cfg)
    
    # Move to filtered dataset path
    """
    Dataset path structure:
    dataset/
        meta.csv
        audio/
            file1.wav
            file2.wav
            ...
    """
    l.info(f"Moving files from final dataset to {DATASET_PATH_FILTERED}")
    AUDIO_PATH = os.path.join(DATASET_PATH_FILTERED, "audio")
    os.makedirs(AUDIO_PATH, exist_ok=True)
    aug_k_df = copy_update_dataset_file(aug_k_df, AUDIO_PATH)
    
    # Save augmented dataframe to .csv
    org_filename = get_filename_without_extension(FULL_META_CSV)
    aug_filename = f"{org_filename}.augmented.folded.csv"
    final_meta = os.path.join(DATASET_PATH_FILTERED, aug_filename)
    l.info(f"Datasets processing done, saving meta file to {final_meta}")
    aug_k_df.to_csv(final_meta, index=False)
    
    

def get_args():
    """
    Get arguments
    """
    import argparse
    parser = argparse.ArgumentParser(description="Workflow")
    parser.add_argument("--clean-cache", help="Clean cached dataset processes", action="store_true")
    return parser.parse_args()

if __name__ == "__main__":
    args = get_args()
    if args.clean_cache == True:
        from utils.file_utils import clean_user_cache_dir
        l.info("Cleaning user cache dir ...")
        c_dir = clean_user_cache_dir()
        l.info(f"Contents in {c_dir} has been cleaned.")
    try:
        workflow()
    except Exception as e:
        l.error(f"Error while executing workflow: {e}")
        l.info(f"Exiting with code 1, full log saved to {C.LOG_PATH}")
        exit(1)