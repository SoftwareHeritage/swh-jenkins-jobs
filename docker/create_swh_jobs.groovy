// Jenkins script inspired from
// https://github.com/peterjenkins1/jenkins-scripts/blob/master/add-xml-job.groovy
// to automatically configue SWH jobs when jenkins starts

import hudson.model.Hudson
import hudson.model.Queue
import hudson.slaves.Cloud
import jenkins.model.Jenkins
import com.cloudbees.hudson.plugins.folder.*
import com.nirima.jenkins.plugins.docker.DockerCloud
import com.nirima.jenkins.plugins.docker.DockerTemplate
import com.nirima.jenkins.plugins.docker.DockerTemplateBase
import io.jenkins.docker.client.DockerAPI
import io.jenkins.docker.connector.DockerComputerAttachConnector
import org.jenkinsci.plugins.docker.commons.credentials.DockerServerEndpoint
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval.PendingScript
import jenkins.model.JenkinsLocationConfiguration

def folderName = 'jenkins-tools'
def jobName = 'jenkins-jobs-builder'
def jobXml = '''<?xml version='1.1' encoding='UTF-8'?>
<project>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <scm class="hudson.scm.NullSCM"/>
  <assignedNode>built-in</assignedNode>
  <canRoam>false</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders>
    <hudson.tasks.Shell>
      <command>
        mkdir -p swh-jenkins-jobs
        cp -rf /opt/swh-jenkins-jobs/* swh-jenkins-jobs/
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

Hudson hudson = Hudson.instance
Jenkins jenkins = Jenkins.instance

// Increase number of executors from 2 to 6
hudson.setNumExecutors(6)
hudson.save()
hudson.setNodes(hudson.getNodes())

jenkins.setLabelString('built-in')

// register swh-sphinx docker node for documentation build
cloud = new DockerCloud(
  'docker',
  new DockerAPI(new DockerServerEndpoint('unix:///var/run/docker.sock', '')),
  []
)
swhSphinxBase = new DockerTemplateBase(
  'container-registry.softwareheritage.org/swh/infra/ci-cd/swh-jenkins-dockerfiles/sphinx:latest'
)
cloud.addTemplate(
  new DockerTemplate(swhSphinxBase, new DockerComputerAttachConnector(), 'swh-sphinx', '/home/jenkins', ''))
jenkins.clouds.add(cloud)

jenkins.save()

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
build = job.scheduleBuild2(0, new hudson.model.Cause.UserIdCause())

build.get()

def scriptApproval = ScriptApproval.get()

for (PendingScript script : scriptApproval.getPendingScripts().clone()) {
  scriptApproval.approveScript(script.getHash())
}

// Approve some groovy function signatures used in custom scripts
String[] signatures = [
  'staticMethod org.codehaus.groovy.runtime.DefaultGroovyMethods toUnique java.util.List',
  'staticMethod jenkins.model.Jenkins getInstance',
  'method jenkins.model.Jenkins getItemByFullName java.lang.String',
  'staticMethod java.net.URLEncoder encode java.lang.String',
]

for (String signature : signatures) {
  scriptApproval.approveSignature(signature)
}

scriptApproval.save()

// Ensure setting of the JOB_URL/BUILD_URL Jenkins environment variables
jlc = JenkinsLocationConfiguration.get()
jlc.setUrl('http://localhost:8080/')
jlc.save()

jenkins.getItem('jenkins-tools').getItem('setup-throttle-categories').scheduleBuild()
