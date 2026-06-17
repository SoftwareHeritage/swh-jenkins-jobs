# Jenkins jobs configuration

This repository manages the jenkins jobs configuration.

A [jenkins jobs] applies the configuration on jenkins when a commit is done in this repository

[jenkins jobs]: https://jenkins.softwareheritage.org/job/jenkins-tools/job/swh-jenkins-job-builder


# Testing

To test locally the configuration , simply run ``tox``:

```
tox
```

The output displays the jenkins configuration files as they will be applied on the
server.

# Run on docker

Jenkins jobs configuration can be tested on a local temporary Jenkins instance executed
in a docker container. The local ``swh-jenkins-jobs`` repository will be mounted as a
volume and copied by Jenkins before calling `tox` in it to setup the jobs.

Jenkins can be executed using two configurations:
- one using a single built-in node
- the other using a single built-in node plus two docker swarm nodes

## Start Jenkins

### Single node

Execute the following script located in the root directory of that repository to
automatically configure the docker image build and start the compose session spawning
a local Jenkins instance with a single built-in node.

```
./start-docker-jenkins.sh
```

### Multiple nodes

Execute the following script located in the root directory of that repository to
automatically configure the docker image build and start the compose session spawning
a Jenkins instance with three nodes: the built-in node and two docker swarm nodes.
```
./start-docker-jenkins-multi-nodes.sh
```

## Stop Jenkins

### Single node

Execute the following script located in the root directory of that repository to stop
the compose session that spawned a local Jenkins instance with a single built-in node.
```
./stop-docker-jenkins.sh
```

### Multiple nodes

Execute the following script located in the root directory of that repository to stop
the compose session that spawned a local Jenkins instance with three nodes.
```
./stop-docker-jenkins-multi-nodes.sh
```

## Authentication

For the single node case, no authentication is required for commodity of use.

For the multiple nodes case, an administration account is required or docker swarm nodes
cannot connect to the Jenkins instance, credentials to use are **admin/admin**.

## SWH jobs registration

Jenkins jobs for Software Heritage should be automatically registered when the jenkins
service is starting.

If the jobs did not get automatically registered, you can trigger their creation by
following these instructions:

- Connect to localhost:8080, then within the jenkins ui:
- Create a jenkins folder `jenkins-tools`
- Create a new `free-style` job named `job-builder` inside the `jenkins-tools`
- Add a `build` step `Execute shell` with this content
```
mkdir -p swh-jenkins-jobs
cp -rf /opt/swh-jenkins-jobs/* swh-jenkins-jobs/
cd swh-jenkins-jobs
tox -- update --delete-old --jobs-only
```
- Save your build configuration
- Trigger a build \o/

This will configure the jobs in your local Jenkins instance.
