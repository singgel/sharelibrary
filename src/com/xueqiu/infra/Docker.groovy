package com.xueqiu.infra

def build() {
    log.i '开始Docker镜像构建'

    def container_env   = Config.settings.container_env
    def container_proj  = Config.settings.container_proj
    def build_zip_path  = Config.settings.build_zip_path
    def build_zip_file  = Config.settings.build_zip_file
    def build_unzip_dir = Config.settings.build_unzip_dir

    def harborDomain = Config.settings.harbor_domain
    def crt = libraryResource("ca.crt")
    sh "mkdir -p /etc/docker/certs.d/$harborDomain"
    sh "echo '$crt' > /etc/docker/certs.d/$harborDomain/ca.crt"

    def version      = Config.settings.git_version
    def stopShell    = libraryResource("shell/deploy_1_stop.sh")
    def startShell   = libraryResource("shell/deploy_3_start.sh")
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

    Config.settings.image_version = "${container_env}-${version}"
    sh "docker build -t ${Config.settings.repository_group}/${container_proj}:${Config.settings.image_version} -f ./Dockerfile ."
    sh "docker images"
    log.i '构建完成'
}


def uploadToHarbor() {
    log.i '将构建结果上传到Harbor镜像仓库'

    def container_proj = Config.settings.container_proj
    def harborDomain  = Config.settings.harbor_domain

    def oldImageName = "${Config.settings.repository_group}/${container_proj}:${Config.settings.image_version}"
    def newImageName = "$harborDomain/${Config.settings.repository_group}/${container_proj}:${Config.settings.image_version}"

    sh "docker login $harborDomain -u admin -p Xq-Harbor-Aliyun-K8s"
    sh "docker tag $oldImageName $newImageName"
    sh "docker push $newImageName"
    sh "docker logout $harborDomain"
    log.i '传送完毕,开始删除本地镜像'
    sh "docker rmi -f $oldImageName"
    sh "docker rmi -f $newImageName"
    log.i "删除完成"
}

def deploy() {
    log.i '开始部署'

    def container_proj = Config.settings.container_proj
    def container_env  = Config.settings.container_env
    def version        = Config.settings.git_version

    String deploymentFile = libraryResource("k8s/deployment.yaml")

    deploymentFile = deploymentFile.replaceAll("\\{\\{CONTAINER_PROJ}}","${container_proj}")
    deploymentFile = deploymentFile.replaceAll("\\{\\{CONTAINER_ENV}}","${container_env}")
    deploymentFile = deploymentFile.replaceAll("\\{\\{GIT_VERSION}}","${version}")
    writeFile file: './deployment.yaml', text: deploymentFile

    sh "cat ./deployment.yaml"

    sh "kubectl apply -f ./deployment.yaml"

    log.i '部署命令下发完成'

    Webhook.info '部署命令下发完成'
}

return this