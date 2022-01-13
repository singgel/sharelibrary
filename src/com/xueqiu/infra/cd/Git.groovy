package com.xueqiu.infra.cd

def clone(repo) {
    git url:"${repo}"
    log.i 'git clone完成'
}

return this