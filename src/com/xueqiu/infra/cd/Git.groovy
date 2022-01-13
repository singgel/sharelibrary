package com.xueqiu.infra.cd

def clone(branch) {
    log.i '分支:' + branch
    sh("pwd")
    sh("git checkout ${branch}")
    sh 'mvn clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -gs /home/jenkins/settings/settings.xml'
    log.i '打包完成'
}

return this