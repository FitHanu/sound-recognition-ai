import os
import subprocess
import importlib
import json
import sys

from logging_cfg import get_logger
l = get_logger(__name__)

from constants import PY_PROJECT_ROOT, SITE_PKG_PATH

#Module registry
MODULE_REG = [
    os.path.join(SITE_PKG_PATH, PY_PROJECT_ROOT),
    os.path.join(SITE_PKG_PATH, PY_PROJECT_ROOT, "utils"),
    os.path.join(SITE_PKG_PATH, PY_PROJECT_ROOT, "ds"),
    os.path.join(SITE_PKG_PATH, PY_PROJECT_ROOT, "partition"),
]
    

def install_requirements():
    req_file = os.path.join(PY_PROJECT_ROOT, "requirements.txt")
    args = ["pip", "install", "-r", req_file]
    try:
        subprocess.call(args)
        l.info(f"Successfully installed dependencies from requirements.txt")
    except subprocess.CalledProcessError as e:
        l.error(f"Failed to install requirements: {e}")


def append_project_path():
    """
    Python modules type shit
    """
    pth_file = SITE_PKG_PATH + os.path.sep + "sra.pth"
    with open(pth_file, "w") as f:
        for module in MODULE_REG:
            f.write(module + "\n")

def validate_packages(package_names):
    """Checks if required packages are available after installation."""
    for package in package_names:
        try:
            importlib.import_module(package)
            l.info(f"✅ {package} is available.")
        except ImportError:
            l.info(f"❌ {package} is missing or failed to install.")
            
def force_reinstall_kaggle():
    args = ["pip", "install", "--upgrade", "--force-reinstall", "--no-deps", "kaggle"]
    subprocess.run(args)
    
def init_split_ds_config(overwrite = False):
    """
    Will init partition configs to config.json
    
    """
    from constants import CONFIG_JSON
    pt_key = "partition"
    with open(CONFIG_JSON, "r") as f:
        config = json.load(f)
        if pt_key in config and overwrite == False:
            l.warning(f"Overwrite is set to off but \"{pt_key}\" key is already exists. Skipping ...")
            pass
        else:
            # Default partition config
            config = {pt_key: {
                "train": 0.8,
                "dev": 0.1,
                "test": 0.1
            }, **config}
    
    with open(CONFIG_JSON, "w") as f:
        json.dump(config, f, indent=2)
    
    
def main():
    l.info("Setting up project")
    steps = [
        [f"Installing requirements", install_requirements],
        [f"Appending project paths to {SITE_PKG_PATH}", append_project_path],
        [f"Initialize default paritioning config", init_split_ds_config],
        # "force_reinstall_kaggle": [force_reinstall_kaggle],
        # [f"Validating packages ...", validate_packages, [["ds.dataset", "utils.json_utils"]]]
    ]

    for step in steps:
        try:
            l.info(f"Running: {step[0]}")
            step[1]()
        except Exception as e:
            l.error(f"Failed to run: {step[0]}")
            l.error(e)
            sys.exit(1)
            
    


if __name__ == "__main__":
    main()