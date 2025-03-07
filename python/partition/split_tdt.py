"""
Steps:

1. get ratio from config.json
2. read the full meta csv
3. determine the median of datapoints of all classes
4. for each classname
    (update dataframe (2) in place)
    if number of classname datapoints < median
        sample up
    else
        sample down
5. write the augmented dataframe to the meta directory
6. Spliting (K-Fold Type shit)
    
    Total number of datapoints (after augmented) = n
    Number of folds: k
    Number of classnames: c
    Points per classname/ fold: p = n/k/c
    
    Train: k * 0.8 (folds)
    Dev: k * 0.1 (folds)
    Test: k * 0.1 (folds)
"""

from utils.json_utils import get_config_json
from utils.csv_utils import read_csv_as_dataframe
from constants import FULL_META_CSV


def main():
    
    data = get_config_json()
    print(data["partition"])
    df = read_csv_as_dataframe(FULL_META_CSV)
    print(df.shape)

if __name__ == "__main__":
    main()