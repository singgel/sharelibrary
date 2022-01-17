package com.xueqiu.infra

def build(container_env, container_proj, build_zip_path, build_zip_file, build_unzip_dir) {
    def version = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    sh "mkdir -p /etc/docker/certs.d/xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com"
    sh "echo '-----BEGIN CERTIFICATE-----\n" +
            "MIIDEzCCAfugAwIBAgIQMEl2iGP3MmlNmIXeN5W+7DANBgkqhkiG9w0BAQsFADAU\n" +
            "MRIwEAYDVQQDEwloYXJib3ItY2EwHhcNMjIwMTEzMDc1NjE4WhcNMjMwMTEzMDc1\n" +
            "NjE4WjAUMRIwEAYDVQQDEwloYXJib3ItY2EwggEiMA0GCSqGSIb3DQEBAQUAA4IB\n" +
            "DwAwggEKAoIBAQDDNfHPQzKFm5ZtGf+svrZJhgve5CoRYQ/Qa1Q7YJC7Pn0a+UGf\n" +
            "pH5Aq2wK5bvTXUp+X4X/StEgpMuvOhuhaJKsKDBvDydbIZf3VmH7IrD+KUk+XMtO\n" +
            "bKeMGS9NHDPEY2oLC2omSATPXIscsJmFrBY4G3PvFB/oCzKKiHIPCWnHV9ygs6FQ\n" +
            "Hwjc3Kh4c1k0zjYAoiNvrhEsIrr3Ev359CVTX2mG+OC8/Q5zmMx3onyvMushsKWH\n" +
            "uuMxuoqIZ5hMWFFehp/MjbcQz2BBgbRnAAjXAT0sAThiFZjTgV5Llrd2iHLfZK5e\n" +
            "skG145/BRAun/1f/CxS2z0gQuaOXOIUuowlVAgMBAAGjYTBfMA4GA1UdDwEB/wQE\n" +
            "AwICpDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDwYDVR0TAQH/BAUw\n" +
            "AwEB/zAdBgNVHQ4EFgQUdVV3mENmafG8oe3ryRon4rvKavYwDQYJKoZIhvcNAQEL\n" +
            "BQADggEBACAmz36fuGOZOPt9Aixzx5TOwnWuntWDGaQJu0AzLE3RKQ8T5XsnxDen\n" +
            "7SzUmzSz5ikx61cKgIBFg9/UCOKibXVI4GtUgstwgPec7XZLgg225yySPfdNxaVQ\n" +
            "fGOjTq4tZXbuSm95Izty87vPkYWn+R7LdAt0hiXqAyw/jKmXR3qLSkmobZdOOT6j\n" +
            "a7l79WxRHKIa0jUh5QB34ZzJ1B8QCc/rkW/Sp/9RakhgK4AFmK1P1Izr1DoIGKmb\n" +
            "mIFHlUwGeAOQqMfsSUODqxFvNBg6cL35zbdXkZ9j2aEcFD3VGUkd8OX0G+cLdgpv\n" +
            "X1LcPyW1wsUELAYDUM/JqFq1fCbVGNg=\n" +
            "-----END CERTIFICATE-----' >> /etc/docker/certs.d/xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/ca.crt"

    log.i '开始Docker镜像构建'
    def dockerFile = libraryResource("docker/Dockerfile")
    def stopShell = libraryResource("shell/deploy_1_stop.sh")
    def startShell = libraryResource("shell/deploy_3_start.sh")
    def replaceShell = libraryResource("shell/deploy_2_replace.sh")

    writeFile file: './Dockerfile', text: dockerFile
    writeFile file: './deploy_1_stop.sh', text: stopShell
    writeFile file: './deploy_3_start.sh', text: startShell
    writeFile file: './deploy_2_replace.sh', text: replaceShell

//    sh "sed -i 's/{{CONTAINER_ENV}}/${container_env}/g' ./Dockerfile"
//    sh "sed -i 's/{{CONTAINER_PROJ}}/${container_proj}/g' ./Dockerfile"
//    sh "sed -i 's/{{BUILD_ZIP_PATH}}/${build_zip_path}/g' ./Dockerfile"
//    sh "sed -i 's/{{BUILD_ZIP_FILE}}/${build_zip_file}/g' ./Dockerfile"
//    sh "sed -i 's/{{BUILD_UNZIP_DIR}}/${build_unzip_dir}/g' ./Dockerfile"
//    sh "cat ./Dockerfile"

    String path = sh("pwd")
    File file = new File("$path/Dockerfile")
    String text = file.text
    text = text.replaceAll("\\{\\{CONTAINER_EN}}","${container_env}")
    text = text.replaceAll("\\{\\{CONTAINER_PROJ}}","${container_proj}")
    text = text.replaceAll("\\{\\{BUILD_ZIP_PATH}}","${build_zip_path}")
    text = text.replaceAll("\\{\\{BUILD_ZIP_FILE}}","${build_zip_file}")
    text = text.replaceAll("\\{\\{BUILD_UNZIP_DIR}}","${build_unzip_dir}")
    sh "echo $text > Dockerfile"
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