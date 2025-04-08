## Setup

1. Run setup

```
python setup.py
```

## Process & implement steps for new dataset

0. Register the new dataset entry to `datasets.json`
1. Create a new `.py` file which filename is the dataset `key` name registered to `datasets.json`
2. Create the dataset class, extend `DataSet` from `ds.dataset.py`
3. Implement methods except `hellyeah()`
4. Add the `new class instance` to `datasets_registry` in `workflow.py`
5. Run `workflow.py` script
6. Manually map class of original dataset to system default class name in `config.json.classmapping.default`
7. Re-run `workflow.py` script :D