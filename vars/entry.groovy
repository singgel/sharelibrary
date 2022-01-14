import com.xueqiu.infra.Git
import com.xueqiu.infra.Docker

def call() {

    log.i 'input params'

    def git = new Git()
    def docker = new Docker()

    def branchName = "${env.git_branchName}"
    def credentialsId = "${env.git_credentialsId}"
    def repo = "${env.git_repo}"
    def projectName = "${env.projectName}"
    def version = "${env.git_version}"
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
                            stage('下载源码') {
                                steps {
                                    script {
                                        git.clone(branchName,repo,credentialsId)
                                    }
                                }
                            }
                            stage('打包') {
                                steps {
                                    script {
                                        git.build()
                                    }
                                }
                            }
                            stage('生成镜像') {
                                steps {
                                   script {
                                       docker.build(projectName,version,environment)
                                       docker.uploadToHarbor(projectName,version)
                                   }
                                }
                            }
                            stage('上传镜像') {
                                steps {
                                    script {
                                        docker.uploadToHarbor(projectName,version)
                                    }
                                }
                            }
                        }
            }

}