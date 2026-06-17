#!/bin/bash -x

set -m

cleanup() {
    echo "Jenkins is shutting down, removing all running job containers ..."
    curl --retry 7 --user admin:admin --data-urlencode \
        "script=$(< /docker/cancel_all_builds.groovy)" \
        http://localhost:8080/scriptText
    containers_to_kill=$(docker ps | grep swh-jenkins-dockerfiles | cut -d' ' -f1)
    docker rm -f $containers_to_kill
}

# ensure to not leak containers executing jobs when compose session is shutting down
trap cleanup EXIT

/usr/bin/tini -s -- /docker/start_jenkins.sh
