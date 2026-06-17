#!/bin/bash

docker_gid=$(stat -c '%g' /var/run/docker.sock)
docker compose build --pull --build-arg DOCKER_GROUP=$docker_gid
docker compose -f compose.yml -f compose.multi-nodes.yml up -d
