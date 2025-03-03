import pandas as pd
import matplotlib.pyplot as plt
import constants as C


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