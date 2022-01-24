package com.xueqiu.infra

def Unzip() {

    try {
        sh "unzip ./resources/apache-maven-3.6.1-bin.zip -d /usr/local"
        sh "ls /usr/local/apache-maven-3.6.1"
        sh "mvn -v"
    }
    catch (e) {
        throw e
    }
}

return this