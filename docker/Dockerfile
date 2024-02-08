FROM jenkins/jenkins:jdk11

USER root

# use same uid and gid for jenkins user/group as in SWH docker images
# (https://gitlab.softwareheritage.org/swh/infra/ci-cd/swh-jenkins-dockerfiles)
# used in jenkins jobs to avoid permission errors when running docker
# in jenkins pipelines
RUN groupmod -g 120 jenkins
RUN usermod -u 115 -g 120 jenkins
RUN find / -group 1000 -exec chgrp -h jenkins '{}' \; || true
RUN find / -user 1000 -exec chown -h jenkins '{}' \; || true

# install tox required to bootstrap jenkins jobs configuration
RUN apt-get update && apt-get -y install tox

# adapt this group if the docker gid is not 998 on your local environment
ARG DOCKER_GROUP=998

# add jenkins user to docker group
RUN addgroup --gid ${DOCKER_GROUP} docker
RUN adduser jenkins docker

USER jenkins
