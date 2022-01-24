package com.xueqiu.infra

def Unzip() {

    try {
        sh "pwd"
        sh "ls"
        sh "unzip ./resources/apache-maven-3.6.1-bin.zip /usr/local"
        sh "ls /usr/local"
    }
    catch (e) {
        throw e
    }
}

return this