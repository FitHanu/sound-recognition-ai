# Steps

### Mỗi một dataset sẽ có một script để filter data, với các steps như sau

1. Tải dataset xuông (Kaggle, tải zip, tar.gz, ....)
2. Extract ra (kaggle thì thôi)
3. Map các sound class mặc định của dataset với từng class mà mình đã định nghĩa sẵn trong file /proto/classes.proto/SoundClass
4. Script lọc từng sound tương ứng, label sao cho đúng với class name mà mình đã định nghĩa sẵn, tổng hợp vào 1 file dataset chung