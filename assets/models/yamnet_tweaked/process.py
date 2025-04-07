"""
General format
id,class_name,severity
0,air_conditioner,NONE
1,alarm_clock,NONE
2,angry,LOW
....


Indices start from 0
"""

import os
import pandas as pd
from pathlib import Path

# Get the parent directory of the script
parent_dir = Path(__file__).parent
csv_path = os.path.join(parent_dir, 'classes.csv')
csv_result_path = os.path.join(parent_dir, 'classes_default_config.csv')

df = pd.read_csv(csv_path)
df['id'] = df['id'] - df['id'].min()
df.to_csv(csv_result_path, index=False)