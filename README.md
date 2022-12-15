# Jenkins jobs configuration

This repository manages the jenkins jobs configuration.

A [jenkins jobs] applies the configuration on jenkins when a commit is done in this repository

[jenkins jobs]: https://jenkins.softwareheritage.org/job/jenkins-tools/job/swh-jenkins-job-builder


# Testing

To test locally the configuration , simply run ``tox``:

```
tox
```

The output displays the jenkins configuration files as they will be applied on the server.

# Run on docker

Jenkins jobs configuration can be tested on a local temporary Jenkins instance
executed in a docker container. The local ``swh-jenkins-jobs`` repository will be
mounted as a volume and cloned by Jenkins so do not forget to commit the changes
you want to test.

- Launch jenkins
```
docker-compose build
docker-compose up
```

Connect to localhost:8080, then within the jenkins ui:
- Create a jenkins folder `jenkins-tools`
- Create a new `free-style` job named `job-builder` inside the `jenkins-tools` targeting
  this git repository `file:///opt/swh-jenkins-jobs`
  - Configure the branch (e.g. `*/master`)
  - Add a `build` step `Execute shell` with this content
```
tox -- update --delete-old
```
- Save your build configuration
- Trigger a build \o/
