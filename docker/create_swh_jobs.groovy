// Jenkins script inspired from
// https://github.com/peterjenkins1/jenkins-scripts/blob/master/add-xml-job.groovy
// to automatically configue SWH jobs when jenkins starts

import hudson.model.Queue
import jenkins.model.Jenkins
import com.cloudbees.hudson.plugins.folder.*

def folderName = 'jenkins-tools'
def jobName = 'jenkins-jobs-builder'
def jobXml = '''<?xml version='1.1' encoding='UTF-8'?>
<project>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders>
    <hudson.tasks.Shell>
      <command>
        git config --global --add safe.directory /opt/swh-jenkins-jobs/.git
        git clone file:///opt/swh-jenkins-jobs
        cd swh-jenkins-jobs
        tox -- update --delete-old --jobs-only
      </command>
      <configuredLocalRules/>
    </hudson.tasks.Shell>
  </builders>
  <publishers/>
  <buildWrappers/>
</project>
'''

Jenkins jenkins = Jenkins.instance // saves some typing

// Get the folder where this job should be
def folder = jenkins.getItem(folderName)
// Create the folder if it doesn't exist
if (folder == null) {
  folder = jenkins.createProject(Folder.class, folderName)
}

def xmlStream = new StringBufferInputStream(jobXml)

// Check if the job already exists
def job = folder.getItem(jobName)
// Remove it if it already exists
if (job != null) {
  folder.remove(job)
}
// Create job in the folder
job = folder.createProjectFromXML(jobName, xmlStream)

// Schedule job execution
Queue.instance.schedule(job, 0)

