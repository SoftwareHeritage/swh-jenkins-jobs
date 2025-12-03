// Jenkins script inspired from
// https://github.com/peterjenkins1/jenkins-scripts/blob/master/add-xml-job.groovy
// to automatically configue SWH jobs when jenkins starts

import hudson.model.Hudson
import hudson.model.Queue
import jenkins.model.Jenkins
import com.cloudbees.hudson.plugins.folder.*
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import jenkins.model.JenkinsLocationConfiguration

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
        mkdir -p swh-jenkins-jobs
        cp -rf /opt/swh-jenkins-jobs/* swh-jenkins-jobs/
        cd swh-jenkins-jobs
        tox -- update --delete-old --jobs-only
      </command>
      <configuredLocalRules/>
    </hudson.tasks.Shell>
  </builders>
  <publishers>
    <hudson.tasks.BuildTrigger>
      <childProjects>jenkins-tools/setup-throttle-categories</childProjects>
      <threshold>
        <name>SUCCESS</name>
        <ordinal>0</ordinal>
        <color>BLUE</color>
        <completeBuild>true</completeBuild>
      </threshold>
    </hudson.tasks.BuildTrigger>
  </publishers>
  <buildWrappers/>
</project>
'''

Hudson hudson = Hudson.instance
Jenkins jenkins = Jenkins.instance

// Increase number of executors from 2 to 6
hudson.setNumExecutors(6)
hudson.save()
hudson.setNodes(hudson.getNodes())

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

def scriptApproval = ScriptApproval.get()

// Approve some groovy function signatures used in custom scripts
String[] signatures = [
  "staticMethod org.codehaus.groovy.runtime.DefaultGroovyMethods toUnique java.util.List",
  "staticMethod jenkins.model.Jenkins getInstance",
  "method jenkins.model.Jenkins getItemByFullName java.lang.String",
  "staticMethod java.net.URLEncoder encode java.lang.String",
]

for (String signature : signatures) {
  scriptApproval.approveSignature(signature)
}

scriptApproval.save()

// Ensure setting of the BUILD_URL Jenkins environment variable
jlc = JenkinsLocationConfiguration.get()
jlc.setUrl("http://localhost:8080/")
jlc.save()
