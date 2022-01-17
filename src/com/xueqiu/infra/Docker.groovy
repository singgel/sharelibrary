package com.xueqiu.infra

def build(container_env, container_proj, build_zip_path, build_zip_file, build_unzip_dir) {
    log.i '开始Docker镜像构建'

    def harboarDomain = Config.settings.harbor_domain
    def crt = libraryResource("ca.crt")
    sh "mkdir -p /etc/docker/certs.d/$harboarDomain"
    sh "echo '$crt' > /etc/docker/certs.d/$harboarDomain/ca.crt"
    sh "cat /etc/docker/certs.d/$harboarDomain/ca.crt"

    def version = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    def stopShell = libraryResource("shell/deploy_1_stop.sh")
    def startShell = libraryResource("shell/deploy_3_start.sh")
    def replaceShell = libraryResource("shell/deploy_2_replace.sh")
    writeFile file: './deploy_1_stop.sh', text: stopShell
    writeFile file: './deploy_3_start.sh', text: startShell
    writeFile file: './deploy_2_replace.sh', text: replaceShell

    String dockerFile = libraryResource("docker/Dockerfile")
    dockerFile = dockerFile.replaceAll("\\{\\{CONTAINER_ENV}}","${container_env}")
    dockerFile = dockerFile.replaceAll("\\{\\{CONTAINER_PROJ}}","${container_proj}")
    dockerFile = dockerFile.replaceAll("\\{\\{BUILD_ZIP_PATH}}","${build_zip_path}")
    dockerFile = dockerFile.replaceAll("\\{\\{BUILD_ZIP_FILE}}","${build_zip_file}")
    dockerFile = dockerFile.replaceAll("\\{\\{BUILD_UNZIP_DIR}}","${build_unzip_dir}")
    writeFile file: "./Dockerfile", text: dockerFile
    sh "cat ./Dockerfile"


    sh "chmod 777 ./deploy_1_stop.sh"
    sh "chmod 777 ./deploy_3_start.sh"
    sh "chmod 777 ./deploy_2_replace.sh"

    sh "docker build -t lib/${container_proj}:${container_env}-${version} -f ./Dockerfile ."
    sh "docker images"
    log.i "删除镜像"
    sh "docker ps -q | xargs docker rmi -f"
    log.i '构建完成'
}


def uploadToHarbor(container_proj, container_env) {
    log.i '将构建结果上传到Harbor镜像仓库'
    def harboarDomain = Config.settings.harbor_domain
    def version = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    sh "docker login $harboarDomain -u admin -p Xq-Harbor-Aliyun-K8s"
    sh "docker tag lib/${container_proj}:${container_env}-${version} $harboarDomain/lib/${container_proj}:${container_env}-${version}"
    sh "docker push $harboarDomain/lib/${container_proj}:${container_env}-${version}"
    sh "docker logout $harboarDomain"
    log.i '传送完毕'
}


def deploy(container_proj, container_env) {
    log.i '开始部署'

    def version = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    String deploymentFile = libraryResource("k8s/deployment.yaml")

    deploymentFile = deploymentFile.replaceAll("\\{\\{CONTAINER_PROJ}}","${container_proj}")
    deploymentFile = deploymentFile.replaceAll("\\{\\{CONTAINER_ENV}}","${container_env}")
    deploymentFile = deploymentFile.replaceAll("\\{\\{GIT_VERSION}}","${version}")
    writeFile file: './deployment.yaml', text: deploymentFile

    sh "cat ./deployment.yaml"

    sh "kubectl apply -f ./deployment.yaml"

    log.i '部署命令下发完成'
}

return this