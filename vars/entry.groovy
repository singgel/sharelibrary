import com.xueqiu.infra.cd.Git

def call(Map config=[:]) {

    log.i 'input params'

   def git = new Git()

    parameters {
        gitParameter (
                branch: '',
                branchFilter: 'origin/(.*)',
                defaultValue: 'master',
                listSize: '10',
                name: 'GIT_REVISION',
                quickFilterEnabled: true,
                selectedValue: 'NONE',
                sortMode: 'DESCENDING_SMART',
                tagFilter: '*',
                type: 'PT_BRANCH_TAG',
                description: 'Please select a branch or tag to build',
                useRepository: Config.data.git_repo)

        choice(
                name: 'ENVIRONMENT',
                description: 'Please select Environment',
                choices: 'dev\nqa\nuat\npre\nprd')

        choice(
                name: 'ACTION',
                description: 'Please select action',
                choices: 'deploy\nrollback')

        choice(
                name: 'DEPLOYMENT_MODE',
                description: 'Please select action',
                choices: 'Container\nLegacy\nMixed')
    }

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
                                        git.clone(GIT_REVISION)
                                    }
                                    checkout pipeline
                                    script {
                                        build_tag = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                                    }
                                    sh 'echo${build_tag}'
                                }
                            }
                        }
            }

}