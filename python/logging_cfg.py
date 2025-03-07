import logging
import logging.config
import os


# Define log file location
from constants import PY_PROJECT_ROOT
LOG_DIR = "logs"
LOG_FILE = os.path.join(PY_PROJECT_ROOT, LOG_DIR, "app.log")

# Ensure log directory exists
os.makedirs(LOG_DIR, exist_ok=True)

# Logging configuration
LOGGING_CONFIG = {
    "version": 1,
    "disable_existing_loggers": False,
    "formatters": {
        "detailed": {
            "format": "%(asctime)s | %(levelname)s | %(module)s | %(message)s"
        },
        "simple": {
            "format": "%(levelname)s | %(message)s"
        }
    },
    "handlers": {
        "console": {
            "class": "logging.StreamHandler",
            "formatter": "simple",
            "level": "INFO"
        },
        "file": {
            "class": "logging.FileHandler",
            "filename": LOG_FILE,
            "formatter": "detailed",
            "level": "DEBUG"
        }
    },
    "root": {  # Applies to all loggers
        "handlers": ["console", "file"],
        "level": "DEBUG"
    }
}

# Apply the logging configuration
logging.config.dictConfig(LOGGING_CONFIG)

# Create a logger for modules to use
def get_logger(name):
    return logging.getLogger(name)
