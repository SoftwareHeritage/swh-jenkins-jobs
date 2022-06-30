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

## gogs
Gogs is a forge used to test the local changes without pushing to the SWH forge.
During the first launch, gogs needs to be configured:
- Open a browser to http://localhost:3000`, a configuration page should be displayed
- select `SQLite3` as database
- In the optional parameters section
  - undeploy the administrator parameters
  - specify the username. You can put anything but this documentation will consider using gogs / gogs
    as user and password
  - put anything the email
- create a repository `swh-jenkins-ci` owned by `gogs` with everything as default

## Initialize the local gogs repository

In this working directory, execute the following commands:
```
git remote add gogs http://localhost:3000/gogs/swh-jenkins-ci
git push gogs
```


## jenkins
- Get the admin password in the logs
```
 docker-compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Connect to localhost:8080, then within the jenkins ui with the user `admin`:
- Change the `admin` password to `admin123`
- Create a jenkins folder `jenkins-tools`
- Create a new `free-style` job named `job-builder` inside the `jenkins-tools` targeting
  this git repository `http://gogs:3000/gogs/swh-jenkins-ci.git`(ne careful of the gogs:3000 hostname)
  - Configure the branch (e.g. `*/master`)
  - Add a `build` step `Execute shell` with this content
```
tox update -- --delete-old
```
- Save your build configuration
- Trigger a build \o/
