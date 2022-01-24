package com.xueqiu.infra

def Unzip() {

    try {
        sh "pwd"
        sh "ls"
    }
    catch (e) {
        throw e
    }
}

return this