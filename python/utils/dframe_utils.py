import matplotlib.pyplot as plt
import constants as C
import os
import pandas as pd
import shutil
from utils.file_utils import get_filename_from_path


def plot_classname_distribution(df: pd.DataFrame):
    """
    Plot the distribution of class names in a dataframe
    """

    # Count the occurrences of each class
    class_counts = df[C.DF_CLASS_NAME_COL].value_counts()

    # Plot the bar chart
    plt.figure(figsize=(12, 6))
    class_counts.plot(kind="bar", color="skyblue", edgecolor="black")

    # Formatting the plot
    plt.title("Number of Data Points per Class", fontsize=14)
    plt.xlabel("Class Name", fontsize=12)
    plt.ylabel("Number of Data Points", fontsize=12)
    plt.xticks(rotation=45, ha="right")
    plt.grid(axis="y", linestyle="--", alpha=0.7)

    # Show the plot
    plt.show()

def copy_update_dataset_file(df: pd.DataFrame, dest_path: str) -> pd.DataFrame:
    """
    Copy dataset files from df to the given path\n
    Then, return a copy of the DataFrame with updated file paths.
    """
    cp_df = df.copy()
    os.makedirs(dest_path, exist_ok=True)
    shutil.rmtree(dest_path)
    os.makedirs(dest_path, exist_ok=True)
    
    # Copy files
    new_paths = []
    for old_path in cp_df["file_path"]:
        try:
            org_filename = get_filename_from_path(old_path)
            new_file_path = os.path.join(dest_path, org_filename)
            shutil.copy2(old_path, new_file_path)
            new_paths.append(new_file_path)
        except FileNotFoundError:
            new_paths.append("FILE_NOT_FOUND")

    # Update paths
    cp_df["file_path"] = new_paths
    return cp_df