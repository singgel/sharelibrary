package com.xueqiu.infra

def build(container_env, container_proj, build_zip_path, build_zip_file, build_unzip_dir) {

    def crt = libraryResource("ca.crt")
    log.i 'crt:' + $crt

    def version = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    sh "mkdir -p /etc/docker/certs.d/xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com"
    sh "echo $crt > /etc/docker/certs.d/xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/ca.crt"

    log.i '开始Docker镜像构建'
    def dockerFile = libraryResource("docker/Dockerfile")
    def stopShell = libraryResource("shell/deploy_1_stop.sh")
    def startShell = libraryResource("shell/deploy_3_start.sh")
    def replaceShell = libraryResource("shell/deploy_2_replace.sh")

    writeFile file: "./Dockerfile", text: dockerFile
    writeFile file: './deploy_1_stop.sh', text: stopShell
    writeFile file: './deploy_3_start.sh', text: startShell
    writeFile file: './deploy_2_replace.sh', text: replaceShell

    sh "echo $build_zip_path > build_zip_path.txt"
    def zipPath = sh(returnStdout: true, script: "sed 's/\\//\\\\\\//g' build_zip_path.txt").trim()

    sh "sed -i 's/{{CONTAINER_ENV}}/${container_env}/g' ./Dockerfile"
    sh "sed -i 's/{{CONTAINER_PROJ}}/${container_proj}/g' ./Dockerfile"
    sh "sed -i 's/{{BUILD_ZIP_PATH}}/${zipPath}/g' ./Dockerfile"
    sh "sed -i 's/{{BUILD_ZIP_FILE}}/${build_zip_file}/g' ./Dockerfile"
    sh "sed -i 's/{{BUILD_UNZIP_DIR}}/${build_unzip_dir}/g' ./Dockerfile"
    sh "cat ./Dockerfile"


    sh "chmod 777 ./deploy_1_stop.sh"
    sh "chmod 777 ./deploy_3_start.sh"
    sh "chmod 777 ./deploy_2_replace.sh"

    sh "docker build -t lib/${container_proj}:${container_env}-${version} -f ./Dockerfile ."
    sh "docker images"
    log.i '构建完成'
}


def uploadToHarbor(container_proj, container_env) {
    echo '将构建结果上传到Harbor镜像仓库'
    def version = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    sh "docker login xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com -u admin -p Xq-Harbor-Aliyun-K8s"
    sh "docker tag lib/${container_proj}:${container_env}-${version} xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/lib/${container_proj}:${container_env}-${version}"
    sh "docker push xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/lib/${container_proj}:${container_env}-${version}"
    sh "docker logout xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com"
    echo '传送完毕'
}


def deploy(container_proj, container_env) {
    log.i '开始部署'
    def version = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    def deploymentFile = libraryResource("k8s/deployment.yaml")
    writeFile file: './deployment.yaml', text: deploymentFile

    sh "sed -i 's/{{CONTAINER_PROJ}}/${container_proj}/g' ./deployment.yaml"
    sh "sed -i 's/{{CONTAINER_ENV}}/${container_env}/g' ./deployment.yaml"
    sh "sed -i 's/{{GIT_VERSION}}/${version}/g' ./deployment.yaml"
    sh "cat ./deployment.yaml"

    sh "kubectl apply -f ./deployment.yaml"

    log.i '部署命令下发完成'
}

return this