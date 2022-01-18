import com.xueqiu.infra.Git
import com.xueqiu.infra.Docker

def call() {

    node {
        settings.config()
    }

    def git    = new Git()
    def docker = new Docker()

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
                            stage('部署') {
                                steps {
                                    script {
                                        docker.deploy()
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
                                        deploymentCanary.deployOperation()
                                    }
                                }
                            }
                            stage('等待部署完成') {
                                steps {
                                    script {
                                        deploymentCanary.waitingStable()
                                        deploymentCanary.finishStable()
                                    }
                                }
                            }
                        }
            }

}