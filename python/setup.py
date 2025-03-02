from pathlib import Path
import os
import sys
import subprocess
import site
import importlib

PROJECT_ROOT = Path(__file__).resolve().parent.parent
PY_PROJECT_ROOT = os.path.join(PROJECT_ROOT, "python")
SITE_PKG_PATH = site.getsitepackages()[0]

#Module registry
MODULE_REG = [
    os.path.join(SITE_PKG_PATH, PY_PROJECT_ROOT),
    os.path.join(SITE_PKG_PATH, PY_PROJECT_ROOT, "utils"),
    os.path.join(SITE_PKG_PATH, PY_PROJECT_ROOT, "ds"),
]
    

def install_requirements():
    req_file = os.path.join(PY_PROJECT_ROOT, "requirements.txt")
    args = ["pip", "install", "-r", req_file]
    try:
        subprocess.call(args)
        print(f"Successfully installed dependencies from requirements.txt")
    except subprocess.CalledProcessError as e:
        print(f"Failed to install requirements: {e}")


def append_project_path():
    pth_file = SITE_PKG_PATH + os.path.sep + "sra.pth"
    with open(pth_file, "w") as f:
        for module in MODULE_REG:
            f.write(module + "\n")
    print(sys.path)
    
def validate_packages(package_names):
    """Checks if required packages are available after installation."""
    for package in package_names:
        try:
            importlib.import_module(package)
            print(f"✅ {package} is available.")
        except ImportError:
            print(f"❌ {package} is missing or failed to install.")
    
def main():
    install_requirements()
    append_project_path()
    validate_packages(["ds.dataset", "utils.json_utils"])
    

def test():
    print(site.getsitepackages())

if __name__ == "__main__":
    main()