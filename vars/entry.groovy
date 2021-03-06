import com.heks.infra.DeploymentCanary
import com.heks.infra.Git
import com.heks.infra.Docker
import com.heks.infra.MavenUtil

def call() {
    def git    = new Git()
    def docker = new Docker()
    def deploymentCanary = new DeploymentCanary()
    def mavenUtil = new MavenUtil()
    def skipWait = false

    node {
        settings.config()
        wrap([$class: 'BuildUser']) {
            Config.settings.deploy_user = env.BUILD_USER_ID
        }

    pipeline
            {
                agent
                        {
                            label 'heks-ack-jnlp'
                        }

                tools
                        {
                            jdk 'jdk1.8.0_221'
                            maven 'maven-3.6.1'
                        }
                stages
                        {
                            stage('初始化Maven环境') {
                                steps {
                                    script {
                                        mavenUtil.Unzip()
                                    }
                                }
                            }
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
                            stage('部署金丝雀') {
                                steps {
                                    script {
                                        deploymentCanary.deployCanary()
                                        deploymentCanary.checkCanary()
                                    }
                                }
                                post {
                                    aborted {
                                        script {
                                            deploymentCanary.deleteCanary()
                                        }
                                    }
                                }
                            }
                            stage('部署正式版本') {
                                steps {
                                    script {
                                        deploymentCanary.deployStable()
                                    }
                                }
                            }
                            stage('部署操作') {
                                steps {
                                    script {
                                        skipWait =  deploymentCanary.deployOperation()
                                    }
                                }
                            }
                            stage('等待部署完成') {
                                steps {
                                    script {
                                        if (!skipWait) {
                                            deploymentCanary.waitingStable()
                                            deploymentCanary.finishStable()
                                        }
                                    }
                                }
                            }
                        }
            }

  }
}