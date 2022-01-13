package com.xueqiu.infra.cd

def clone(branch) {
    sh("pwd")
    sh("git checkout ${branch}")
    log.i 'git clone完成'
}

return this