package com.xueqiu.infra

def clone() {

    def branch = Config.settings.git_branchName
    def repo   = Config.settings.git_repo
    def credentialsId   = Config.settings.git_credentialsId

    try {
        log.i "git clone $branch from $repo"

        git credentialsId: credentialsId,
                branch: branch,
                url: repo

        // 查询项目的一些信息
        getProjectGroup()
        getGitVersion()
    }
    catch (e) {
        log.e 'git clone 出现异常'
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
    log.e 'mvn package出现异常'
    throw e
 }
}

def getProjectGroup() {
    def remoteName = sh(returnStdout: true, script: "git remote").trim()
    String pushUrl = sh(returnStdout: true, script: "git remote get-url --push $remoteName").trim()
    String repositoryName = ""
    if (pushUrl.startsWith("http")) {
        String[] array = pushUrl.split("/")
        repositoryName = array[array.length - 2]
    } else {
        String[] array = pushUrl.split(":")
        array = array[array.length - 1].split("/")
        repositoryName = array[0]
    }

    log.i "项目所属分组: $repositoryName"
    Config.settings.repository_group = repositoryName
}

def getGitVersion() {
    Config.settings.git_version = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    log.i "项目version ${Config.settings.git_version}"
}

return this