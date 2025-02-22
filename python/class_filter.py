import json
import urllib.request
import pandas as pd



meta_esc_50 = "https://github.com/karolpiczak/ESC-50/blob/master/meta/esc50.csv"
response = urllib.request.urlopen(meta_esc_50)
with open('esc50.csv', 'wb') as file:
    file.write(response.content)
esc50_df = pd.read_csv('esc50.csv')
classes = esc50_df['category'].unique()
print(classes)