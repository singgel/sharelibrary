import com.xueqiu.infra.Git
import com.xueqiu.infra.Docker
import com.xueqiu.infra.DeploymentCanary

def call() {

    node {
        settings.config()
    }

    def git    = new Git()
    def docker = new Docker()
    def deploymentCanary = new DeploymentCanary()

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
                                        git.clone()
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
                                      docker.build()
                                    }
                                }
                            }
                            stage('上传镜像') {
                                steps {
                                    script {
                                        docker.uploadToHarbor()
                                    }
                                }
                            }
                            stage('替换参数') {
                                steps {
                                    script {
                                        deploymentCanary.sedArg()
                                    }
                                }
                            }
                        }
            }

}