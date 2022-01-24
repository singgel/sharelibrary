package com.xueqiu.infra

def Unzip() {

    try {
        sh "pwd"
    }
    catch (e) {
        throw e
    }
}

return this