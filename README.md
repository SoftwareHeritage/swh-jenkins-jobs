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
volume and cloned by Jenkins so do not forget to commit the changes you want to test.

- Launch jenkins

Executing the following script located in the root directory of that repository will automatically configure the docker image build and start the compose session.
```
./start-docker-jenkins.sh
```

Jenkins jobs for Software Heritage should be automatically registered when the jenkins service is starting.

If the jobs did not get automatically registered, you can trigger their creation by following these instructions:

- Connect to localhost:8080, then within the jenkins ui:
- Create a jenkins folder `jenkins-tools`
- Create a new `free-style` job named `job-builder` inside the `jenkins-tools`
- Add a `build` step `Execute shell` with this content
```
git config --global --add safe.directory /opt/swh-jenkins-jobs/.git
mkdir -p swh-jenkins-jobs
cp -rf /opt/swh-jenkins-jobs/* swh-jenkins-jobs/
cd swh-jenkins-jobs
tox -- update --delete-old --jobs-only
```
- Save your build configuration
- Trigger a build \o/

This will install the jobs in your local jenkins. Jobs that can be run directly on the
built-in node can be executed. Other jobs that may need to run docker needs the docker
agent to be configured.
