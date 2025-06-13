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
csv_path = os.path.join(parent_dir, 'yamnet_class_map.csv')

df = pd.read_csv(csv_path)
print(df.head())
df = df.drop(columns=['mid'])
df = df.rename(columns={'display_name': 'class_name'})
df['severity'] = 'NONE'
df.to_csv('./flutter/assets/models/yamnet/classes.csv', index=False)