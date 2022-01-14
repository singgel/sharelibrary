import com.xueqiu.infra.Git
import com.xueqiu.infra.Docker

def call() {

    log.i 'input params'

    def git = new Git()
    def docker = new Docker()

    def branchName = "${env.branchName}"
    def credentialsId = "${env.credentialsId}"
    def repo = "${env.repo}"
    def projectName = "${env.projectName}"
    def version = "${env.version}"
    def environment = "${env.environment}"

    pipeline
            {
                agent
                        {
                            label 'xueqiu-ack-jnlp'
                        }

                tools
                        {
                            maven 'maven-3.8.4'
                        }
                stages
                        {
                            stage('Checkout') {
                                steps {
                                    echo '从GitHub下载工程的源码'
                                    script {
                                        git.clone(branchName,repo,credentialsId)
                                        git.build()
                                    }
                                }
                            }
                            stage('Docker Build') {
                                steps {
                                   script {
                                       docker.build(projectName,version,environment)
                                       docker.uploadToHarbor(projectName,version)
                                   }
                                }
                            }
                        }
            }

}