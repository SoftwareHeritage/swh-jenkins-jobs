#!/bin/bash -x

# turn on bash's job control
set -m

PLUGINS_FILE=/docker/plugins.txt

if [ -f "$PLUGINS_FILE" ]; then
    echo "Installing plugins from $PLUGINS_FILE ..."
    jenkins-plugin-cli -f $PLUGINS_FILE
fi

/usr/bin/tini -- /usr/local/bin/jenkins.sh&

# create swh CI jobs in jenkins
wait-for-it localhost:8080 -s --timeout=0
curl --retry 7 --data-urlencode \
    "script=$(< /docker/create_swh_jobs.groovy)" \
    http://localhost:8080/scriptText

fg %1
