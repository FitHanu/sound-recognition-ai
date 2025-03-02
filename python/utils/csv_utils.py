from dataset import PD_SCHEMA
from setup import PY_PROJECT_ROOT
import pandas as pd
import os

# Path to the meta directory
META_DIR = os.path.join(PY_PROJECT_ROOT, "ds", "meta")

def validate_dataframe(df: pd.DataFrame, schema: dict) -> bool:
    """
    Validates if a DataFrame follows the given schema.
    
    :param df: The DataFrame to validate
    :param schema: A dictionary defining the expected schema
    :return: True if valid, False otherwise
    """
    # Check if all required columns exist
    missing_columns = [col for col in schema if col not in df.columns]
    if missing_columns:
        print(f"Missing columns: {missing_columns}")
        return False

    # Check if all columns have the correct data type
    for col, expected_dtype in schema.items():
        actual_dtype = str(df[col].dtype)
        if actual_dtype != expected_dtype:
            print(f"Column {col} has incorrect type: expected {expected_dtype}, got {actual_dtype}")
            return False

    print("DataFrame is valid")
    return True


def write_csv_meta(df: pd.DataFrame, file_name: str):
    """
    Write df to csv file in the constant meta directory.
    """
    # TODO: fix the validation lata
    # if validate_dataframe(df, PD_SCHEMA) == False:
    #     raise ValueError("Dataframe validation failed")

    # Ensure the meta directory exists
    os.makedirs(META_DIR, exist_ok=True)

    # Write the dataframe to the meta directory
    file_path = os.path.join(META_DIR, file_name + ".csv")
    df.to_csv(file_path, index=False)