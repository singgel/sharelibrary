package com.xueqiu.infra

def commitID(int len=41){
    return sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()[0..len]
}

def clone(branch,repo,credentialsId) {
    try {
        log.i '克隆 ' + branch + ' from ' + repo

        git credentialsId: credentialsId,
                branch: branch,
                url: repo
    }
    catch (e) {
        log.e 'Ops! Error occurred during git clone'
        throw e
    }
}

def build() {
 try {
    log.i '开始打包'
     sh("pwd")
     sh 'mvn clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -gs /home/jenkins/settings/settings.xml'
     log.i '打包完成'
 }catch(e) {
    log.e 'Ops! Error occurred during mvn package'
    throw e
 }
}

return this