import com.xueqiu.infra.cd.Git

def call(Map config=[:]) {

    log.i 'input params'

   def git = new Git()

    def version = ${env.GIT_REVISION}

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
                                        log.i 'clone repository'
                                        git.clone(version)
                                    }
                                    script {
                                        build_tag = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                                    }
                                    sh 'echo${build_tag}'
                                }
                            }
                        }
            }

}