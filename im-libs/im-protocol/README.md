
### 安装 Protobuf
在使用protobuf 编译相关文件前需要安装相关编译器, 由于本机使用的是 Mac, 故通过
brew 可直接安装:
``` shell script
brew install protobuf
```

### 编译 proto 文件

```shell script
protoc --java_out=src/main/java proto/AuthenticateRequest.proto
```