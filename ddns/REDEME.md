###jar包使用
mvn clean package -PDDNS
###docker使用
mvn clean package -PDDNSDocker

下载镜像
````
docker pull openjdk:8-jre
````
Dockerfile:
````
FROM openjdk:8-jre

MAINTAINER companion "companion@qq.com"

ENV TimeZone=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TimeZone /etc/localtime && echo $TimeZone > /etc/timezone

ARG port
ARG accessKeyId
ARG secret
ARG type
ARG domainName
ARG parseList

ENV port $port
ENV accessKeyId $accessKeyId
ENV secret $secret
ENV type $type
ENV domainName $domainName
ENV parseList $parseList

# 工作目录
WORKDIR /home/ddns

# 对外暴露端口
EXPOSE $port

# 将jar添加到容器
ADD DDNSDocker.jar DDNSDocker.jar

# 启动命令，该节点使用需要将参数传递到ENV节点
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8 -jar /home/ddns/DDNSDocker.jar \
    $accessKeyId \
    $secret \
    $type \
    $domainName \
    $parseList
````
构建docker镜像
````
docker build -f /mnt/data/docker-build/ddns/Dockerfile -t ddns:v1 .
````
docker-compose.yml
````
version: "3"
services:
  ddns:
    image: ddns:v1
    container_name: ddns
    environment:
        JAVA_OPTS: -Dhudson.model.DownloadService.noSignatureCheck=true -Duser.timezone=Asia/Shanghai
        accessKeyId: "xx"
        secret: "xxx"
        type: "A"
        domainName: "xx.com"
        parseList: "aa,bb"
    deploy:
      replicas: 1
      restart_policy:
        condition: on-failure
    networks:
      - default
````
运行
````
docker-compose up -d
````