import com.xueqiu.infra.Git
import com.xueqiu.infra.Docker

def call() {

    log.i 'input params'

    def git = new Git()
    def docker = new Docker()

    def branch_name = "${env.git_branchName}"
    def credentialsId = "${env.git_credentialsId}"
    def repo = "${env.git_repo}"
    def version = "${env.git_version}"
    def container_env = "${env.container_env}"
    def container_proj = "${env.container_proj}"
    def container_zip_file = "${env.container_zip_file}"
    def container_unzip_dir = "${env.container_unzip_dir}"

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
                                        git.clone(branch_name,repo,credentialsId)
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
                                       docker.build(container_env,container_proj,container_zip_file,container_unzip_dir,version)
                                   }
                                }
                            }
                            stage('上传镜像') {
                                steps {
                                    script {
                                        docker.uploadToHarbor(container_proj,branch_name,version)
                                    }
                                }
                            }
                            stage('部署') {
                                steps {
                                    script {
                                        docker.deploy(container_proj,branch_name,version)
                                    }
                                }
                            }
                        }
            }

}