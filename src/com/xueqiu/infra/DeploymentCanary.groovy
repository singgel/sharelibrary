package com.xueqiu.infra

def sedArg() {
    def deployment_name = Config.settings.container_proj
    def image_name = Config.settings.container_proj
    def image_version = Config.settings.image_version
    def ready_time = Config.settings.ready_time
    def max_surge = Config.settings.max_surge
    def replicas_number = Config.settings.replicas_number
    def harbor_domain = Config.settings.harbor_domain
    def repository_group = Config.settings.repository_group

    def deploymentCanaryFile = libraryResource("k8s/deployment-canary.yaml")
    writeFile file: './deployment-canary.yaml', text: deploymentCanaryFile
    def deploymentStableFile = libraryResource("k8s/deployment-stable.yaml")
    writeFile file: './deployment-stable.yaml', text: deploymentStableFile
    
    sh "sed -i 's/{{deployment-name}}/${deployment_name}/g' ./deployment-canary.yaml"
    sh "sed -i 's/{{ready-time}}/${ready_time}/g' ./deployment-canary.yaml"
    sh "sed -i 's/{{image-name}}/${image_name}/g' ./deployment-canary.yaml"
    sh "sed -i 's/{{image-version}}/${image_version}/g' ./deployment-canary.yaml"
    sh "sed -i 's/{{app-name}}/${deployment_name}/g' ./deployment-canary.yaml"
    sh "sed -i 's/{{harbor_domain}}/${harbor_domain}/g' ./deployment-canary.yaml"
    sh "sed -i 's/{{repository_group}}/${repository_group}/g' ./deployment-canary.yaml"
    
    sh "sed -i 's/{{deployment-name}}/${deployment_name}/g' ./deployment-stable.yaml"
    sh "sed -i 's/{{ready-time}}/${ready_time}/g' ./deployment-stable.yaml"
    sh "sed -i 's/{{image-name}}/${image_name}/g' ./deployment-stable.yaml"
    sh "sed -i 's/{{image-version}}/${image_version}/g' ./deployment-stable.yaml"
    sh "sed -i 's/{{app-name}}/${deployment_name}/g' ./deployment-stable.yaml"
    sh "sed -i 's/{{replicas-number}}/${replicas_number}/g' ./deployment-stable.yaml"
    sh "sed -i 's/{{max-surge}}/${max_surge}/g' ./deployment-stable.yaml"
    sh "sed -i 's/{{harbor_domain}}/${harbor_domain}/g' ./deployment-stable.yaml"
    sh "sed -i 's/{{repository_group}}/${repository_group}/g' ./deployment-stable.yaml"
    
    
    sh "cat ./deployment-canary.yaml"
    sh "cat ./deployment-stable.yaml"
    echo '替换参数完成'
}


def deployCanary() {
    def deployment_name = Config.settings.container_proj
    def canary = Config.settings.canary
    cmd("apply -f ./deployment-canary.yaml --record")
    cmd("rollout status deployment ${deployment_name}${canary}")
    echo '部署金丝雀完成'
}

def checkCanary() {
    def deployment_name = Config.settings.container_proj
    def canary = Config.settings.canary
    echo "检查你的金丝雀pod"
    cmd("describe deployment ${deployment_name}${canary}")
    def checkInput = input(
        id: 'checkInput',
        message: '请检查金丝雀服务是否正常',
        parameters: [
            [
                $class: 'ChoiceParameterDefinition',
                choices: "OK\n",
                name: 'result'
            ]
        ]
    )
    echo "This is a deploy step to ${checkInput}"
    cmd("delete deployment ${deployment_name}${canary}")
}

def deleteCanary() {
    def deployment_name = Config.settings.container_proj
    def canary = Config.settings.canary
    echo "删除金丝雀pod: ${deployment_name}${canary}"
    cmd("delete deployment ${deployment_name}${canary}")
}

def deployStable() {
    echo "检查你的正式pod"
    cmd("apply -f ./deployment-stable.yaml --record")
}

def deployOperation() {
    def deployment_name = Config.settings.container_proj
    def stable = Config.settings.stable
    def operationInput = input(
        id: 'operationInput',
        message: '请选择操作',
        parameters: [
            [
                $class: 'ChoiceParameterDefinition',
                choices: "Skip\nPause\nUndo",
                name: 'result'
            ]
        ]
    )

    def skipWait = false
    if (operationInput == "Pause") {
         cmd("rollout pause deployment ${deployment_name}${stable}")
    } else if (operationInput == "Undo"){
        def repository_group = Config.settings.repository_group
        def generation = sh(returnStdout: true, script: "kubectl rollout history deployment ${deployment_name}${stable} " +
                "-o jsonpath='{.metadata.annotations.deployment\\.kubernetes\\.io/revision}' -n ${repository_group}").trim()
        echo "generaion: $generation"
        if (generation == "1") {
            cmd("delete deployment ${deployment_name}${stable}")
            skipWait = true
        } else {
            cmd("rollout undo deployment ${deployment_name}${stable}")
        }
    } else if (operationInput == "Skip"){
        // deploy prod stuff
    }
    return skipWait
}

def waitingStable() {
    def deployment_name = Config.settings.container_proj
    def stable = Config.settings.stable
    echo "等待pod部署完成"
    cmd("rollout status deployment ${deployment_name}${stable}")
}

def finishStable() {
    def deployment_name = Config.settings.container_proj
    def stable = Config.settings.stable
    echo "等待pod部署完成"
    cmd("describe deployment ${deployment_name}${stable}")
}

def String cmd(String c){
    try {
        def repository_group = Config.settings.repository_group
        sh("kubectl $c  -n ${repository_group}")
    }
    catch (e) {
        throw e
    }
}

return this
