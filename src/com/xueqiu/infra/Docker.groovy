package com.xueqiu.infra

def build(projectName,version) {
    log.i '开始Docker镜像构建'
    def dockerFile   = libraryResource("docker/Dockerfile")
    def stopShell    = libraryResource("shell/deploy_1_stop.sh")
    def startShell   = libraryResource("shell/deploy_3_start.sh")
    def replaceShell = libraryResource("shell/deploy_2_replace.sh")

    writeFile file: './Dockerfile', text: dockerFile
    writeFile file: './deploy_1_stop.sh', text: stopShell
    writeFile file: './deploy_3_start.sh', text: startShell
    writeFile file: './deploy_2_replace.sh', text: replaceShell

    sh "sed -i 's/{{PROJECT_NAME}}/${projectName}/g' ./Dockerfile"
    sh "cat ./Dockerfile"

    sh "sed -i 's/{{PROJECT_NAME}}/${projectName}/g' ./deploy_2_replace.sh"
    sh "cat ./deploy_2_replace.sh"

    sh "chmod 777 ./deploy_1_stop.sh"
    sh "chmod 777 ./deploy_3_start.sh"
    sh "chmod 777 ./deploy_2_replace.sh"

    sh "docker login xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com -u admin -p Xq-Harbor-Aliyun-K8s"
    sh "docker build -t lib/${projectName}:${version} -f ./Dockerfile ."
    sh "docker images"
    log.i '构建完成'
}


def uploadToHarbor(projectName,version) {
    echo '将构建结果上传到Harbor镜像仓库'
    sh "docker tag lib/${projectName}:${version} xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/lib/${projectName}:${version}"
    sh "docker push xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/lib/${projectName}:${version}"
    echo '传送完毕'
}


def deploy(projectName,version,environment) {
    log.i '开始部署'
    def deploymentFile = libraryResource("k8s/deployment.yaml")
    writeFile file: './deployment.yaml', text: deploymentFile

    sh "sed -i 's/{{PROJECT_NAME}}/${projectName}/g' ./deployment.yaml"
    sh "sed -i 's/{{ENVIRONMENT}}/${environment}/g' ./deployment.yaml"
    sh "sed -i 's/{{GIT_VERSION}}/${version}/g' ./deployment.yaml"
    sh "cat ./deployment.yaml"

    sh "kubectl apply -f ./deployment.yaml"

    log.i '部署命令下发完成'
}

return this