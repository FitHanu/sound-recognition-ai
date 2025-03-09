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
from typing import Final


class SplitCfg:
    
    FOLDS: int = 0
        
    def __init__(self, tr: float, de: float, ts: float):
        sum = tr + de + ts
        # train + dev + test should be = 1.0 in any case
        self.train = tr / sum
        self.dev = de / sum
        self.test = ts / sum
        
    def __init__(self, tr: float, de: float, ts: float, folds: int):
        self.__init__(tr, de, ts)
        if (folds <= 3):
            raise ValueError("SplitCfg.Folds must be greater than 3 when init")
        self.FOLDS = Final[int]
        self.train_f = int(self.FOLDS * self.train)
        self.dev_f = int(self.FOLDS * self.dev)
        self.test_f = int(self.FOLDS * self.test)

def split_tdt():
    pass


def main(): 
    
    # 1-2 get config, read meta
    data = get_config_json()
    print(data["partition"])
    df = read_csv_as_dataframe(FULL_META_CSV)
    
    # Determine the median of classnames datapoints
    v_counts = df['class_name'].value_counts()
    median_datapoints = df['class_name'].value_counts().median()
    print(v_counts.mean())
    print(f"Median of class datapoints: {median_datapoints}")

if __name__ == "__main__":
    main()