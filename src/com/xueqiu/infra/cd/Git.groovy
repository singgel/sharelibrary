package com.xueqiu.infra.cd

def clone(repo) {
    git url:"${repo}"
}

return this