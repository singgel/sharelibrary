package com.xueqiu.infra.cd

def clone(repo) {
    sh("pwd")
    log.i 'git clone完成'
}

return this