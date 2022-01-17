import com.xueqiu.infra.Git
import com.xueqiu.infra.Docker

def call() {

    log.i 'input params'

    def git = new Git()
    def docker = new Docker()

    def branch_name = "${env.git_branchName}"
    def credentialsId = "${env.git_credentialsId}"
    def repo = "${env.git_repo}"
    def container_env = "${env.container_env}"
    def container_proj = "${env.container_proj}"
    def build_zip_path = "${env.build_zip_path}"
    def build_zip_file = "${env.build_zip_file}"
    def build_unzip_dir = "${env.build_unzip_dir}"

    pipeline
            {
                agent
                        {
                            label 'xueqiu-ack-jnlp'
                        }

                tools
                        {
                            jdk 'jdk1.8.0_221'
                            maven 'maven-3.6.1'
                        }
                stages
                        {
                            stage('下载源码') {
                                steps {
                                    script {
                                        git.clone(branch_name, repo, credentialsId)
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
                                        docker.build(container_env, container_proj, build_zip_path, build_zip_file, build_unzip_dir)
                                    }
                                }
                            }
                            stage('上传镜像') {
                                steps {
                                    script {
                                        docker.uploadToHarbor(container_proj, container_env)
                                    }
                                }
                            }
                            stage('部署') {
                                steps {
                                    script {
                                        docker.deploy(container_proj, container_env)
                                    }
                                }
                            }
                        }
            }

}