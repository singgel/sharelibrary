package com.xueqiu.infra.cd

def clone(branch,repo,credentialsId) {
    try {
        log.i '克隆 ' + branch + ' from ' + repo

        git credentialsId: credentialsId,
                branch: branch,
                url: repo
    }
    catch (e) {
        log.e 'Ops! Error occurred during git checkout'
        throw e
    }
}

def build(branch) {
    log.i 'build 分支:' + branch
    sh("pwd")
    sh 'mvn clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -gs /home/jenkins/settings/settings.xml'
    log.i '打包完成'
}

return this