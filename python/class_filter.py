import urllib.request
import pandas as pd

meta_esc_50 = "https://raw.githubusercontent.com/karolpiczak/ESC-50/master/meta/esc50.csv"
with urllib.request.urlopen(meta_esc_50) as f:
    esc50_df = pd.read_csv(f)
classes = sorted(esc50_df['category'].unique())
print(classes)