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
```
docker-compose build
docker-compose up
```

Connect to localhost:8080, then within the jenkins ui:
- Create a jenkins folder `jenkins-tools`
- Create a new `free-style` job named `job-builder` inside the `jenkins-tools` targeting
  this git repository `file:///opt/swh-jenkins-jobs`
  - Configure the branch your are developing on (e.g. `*/master`, `*/awesome-feature`,
    ...)
  - Add a `build` step `Execute shell` with this content
```
tox -- update --delete-old
```
- Save your build configuration
- Trigger a build \o/

This will install the jobs in your local jenkins. Jobs that can be run directly on the
built-in node can be executed. Other jobs that may need to run docker needs the docker
agent to be configured.

# Configure a docker agent

For making a docker agent runnable, it needs the 50000 port to be
available.

Then, within your local jenkins, click on the local jenkins interface:

> Manage jenkins
> Manage nodes and clouds
> New nodes

Then fill in the form, keeping the default values and adapting the rest:

- name: docker agent
- remote root dir: /var/tmp/jenkins
- labels: docker
- launch method: launch agent by connecting it to the controller
- custom workdir path: /var/tmp/jenkins

Save, then click on 'docker agent' and follow the proposed instructions:

```
$ curl -sO http://localhost:8080/jnlpJars/agent.jar
$ mkdir -p /var/tmp/jenkins
$ java -jar agent.jar \
  -jnlpUrl http://localhost:8080/manage/computer/docker%20agent/jenkins-agent.jnlp \
  -workDir "/var/tmp/jenkins"
```
