package com.xueqiu.infra

def build(projectName,version,environment) {

    log.i '开始Docker镜像构建'
    //执行部署脚本
    def deploymentFile = libraryResource("k8s/deployment.yaml")
    writeFile file: './deployment.yaml', text: deploymentFile
    sh "cat ./deployment.yaml"

    sh "ls -l ${projectName}/target"
    sh "sed -i '#{{PROJECT_NAME}}#${projectName}#g' ./deployment.yaml"
    sh "sed -i '#{{ENVIRONMENT}}#${environment}#g' ./deployment.yaml"
    sh "docker login xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com -u admin -p Xq-Harbor-Aliyun-K8s"
    sh "docker build -t lib/${projectName}:${version} -f docker/Dockerfile ."
    sh "docker images"
}


def uploadToHarbor(projectName,version) {
    echo '将构建结果上传到Harbor镜像仓库'
    sh "docker tag lib/${projectName}:${version} xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/lib/${projectName}:${version}"
    sh "docker push xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/lib/${projectName}:${version}"
    echo '传送完毕'
}

return this