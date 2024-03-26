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
  <scm class="hudson.plugins.git.GitSCM" plugin="git@5.2.1">
    <configVersion>2</configVersion>
    <userRemoteConfigs>
      <hudson.plugins.git.UserRemoteConfig>
        <url>file:///opt/swh-jenkins-jobs</url>
      </hudson.plugins.git.UserRemoteConfig>
    </userRemoteConfigs>
    <branches>
      <hudson.plugins.git.BranchSpec>
        <name>*/master</name>
      </hudson.plugins.git.BranchSpec>
    </branches>
    <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
    <submoduleCfg class="empty-list"/>
    <extensions/>
  </scm>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders>
    <hudson.tasks.Shell>
      <command>tox -- update --delete-old --jobs-only</command>
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

