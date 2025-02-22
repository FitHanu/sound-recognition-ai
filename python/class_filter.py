import json
import urllib.request
import pandas as pd



meta_esc_50 = "https://github.com/karolpiczak/ESC-50/blob/master/meta/esc50.csv"
with urllib.request.urlopen(meta_esc_50) as f:
    esc50_df = pd.read_csv(f.read.decode('utf-8'))
classes = esc50_df['category'].unique()
print(classes)