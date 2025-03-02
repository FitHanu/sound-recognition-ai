from json_utils import get_default_class_mapping
import os


def is_path(path: str) -> bool:
    """
    Validate if the given path is a valid directory path.
    """
    return os.path.isdir(path)

def init_class_folds(path: str):
    """
    Initialize class folders to `path`
    """
    if not is_path(path):
        raise ValueError(f"{path} is not a valid directory path.")

    default_class = get_default_class_mapping()
    for k, _ in default_class.items():
        os.makedirs(os.path.join(path, str(k)), exist_ok=True)