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

- Launch jenkins
```
docker-compose build
docker-compose up
```
- Get the admin password in the logs

Connect to localhost:8080, then within the jenkins ui:
- Change the `admin` password to `admin123`
- Create a jenkins folder `jenkins-tools`
- Create a new `free-style` job named `job-builder` inside the `jenkins-tools` targeting
  this git repository `https://forge.softwareheritage.org/source/swh-jenkins-jobs.git`
  - Configure the branch (e.g. `*/master`)
  - Add a `build` step `Execute shell` with this content
```
tox update -- --delete-old
```
- Save your build configuration
- Trigger a build \o/
