import zipfile
import subprocess
import uuid
import os

# Download datasets
# 1. ESC-50 https://github.com/karoldvl/ESC-50/archive/master.zip
# 2. UrbanSound8k https://goo.gl/8hY5ER

def download(url, file_path=".", file_name=None):
    if file_name is None:
        tmp_name = get_file_name_from_url(url)
        if (tmp_name == ""):
            print("Cannot get file name from URL")
            file_name = str(uuid.uuid4())
        else:
            file_name = get_file_name_from_url(url)
    local_filepath = os.path.join(file_path, file_name)
    print(f"Download {file_name} to {file_path}")
    subprocess.run(["wget", url, "-O", local_filepath])
    print(f"Done downloading, file name: {local_filepath}")
    
    return local_filepath
    

def extract(file_path, output_path="."):
    print(f"Extract {file_path} to {output_path}")
    output_file_path = os.path.join(output_path, remove_extensions(file_path))
    os.makedirs(output_path, exist_ok=True)
    if file_path.endswith(".zip"):
        print("Handling zip file ...")
        with zipfile.ZipFile(file_path, 'r') as zip_ref:
            zip_ref.extractall(output_file_path)
    elif file_path.endswith(".tar.gz"):
        print("Handling .tar.gz file ...")
        subprocess.run(["tar", "-xzvf", file_path, "-C", output_file_path])
    else:
        print(f"Unknown file format {file_path}")
    

def remove_extensions(file_name):
    names = file_name.split(".")
    return names[0]

def get_file_name_from_url(url):
    return url.split('/')[-1]

# URLs of the datasets  print(f"Download {}")
esc50_url = 'https://github.com/karoldvl/ESC-50/archive/master.zip'
urbansound8k_url = 'https://zenodo.org/records/1203745/files/UrbanSound8K.tar.gz'


def download_and_extract(url):
    local_path = download(url=url)
    extract(file_path=local_path)
    print("Done download and extracting dataset")

# download_and_extract(esc50_url)
extract("master.zip")