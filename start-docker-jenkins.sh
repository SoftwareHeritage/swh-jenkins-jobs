#!/bin/bash

docker_gid=$(stat -c '%g' /var/run/docker.sock)
docker compose build --build-arg DOCKER_GROUP=$docker_gid
docker compose up -d
