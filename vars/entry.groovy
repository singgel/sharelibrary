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
                            maven 'maven-3.8.4'
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
                        }
            }

}