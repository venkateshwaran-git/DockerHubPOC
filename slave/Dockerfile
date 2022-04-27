FROM golang:1.17-alpine3.14

RUN apk add --no-cache --update git build-base bash curl openjdk8-jre npm

#Install docker-compose dependencies
#https://docs.docker.com/compose/install/
RUN apk add py-pip \
python3-dev \
libffi-dev \
openssl-dev \
gcc \
libc-dev \
rust \
cargo \
make

#Install Docker CLI
RUN curl "https://s3.amazonaws.com/ezesoft-docker/docker-full-linux-64.tgz" -o "/tmp/docker.tgz" \
    && tar -xvzf /tmp/docker.tgz -C /tmp docker/docker \
    && mv /tmp/docker/docker /usr/local/bin/docker

#Point npm registry to artifactory
RUN npm config set registry "http://bosdevartifactory.ezesoft.net:8081/artifactory/api/npm/npm-eze"

RUN pip install 'docker-compose===1.23.2'

RUN chmod -R a+rwx /root
