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
    sh "kubectl apply -f ./deployment-canary.yaml --record"
    sh "kubectl rollout status deployment ${deployment_name}${canary}"
    echo '部署金丝雀完成'
}

def checkCanary() {
    def deployment_name = Config.settings.container_proj
    def canary = Config.settings.canary
    echo "检查你的金丝雀pod"
    sh "kubectl describe deployment ${deployment_name}${canary}"
    def checkInput = input(
        id: 'checkInput',
        message: 'check your canary pod',
        parameters: [
            [
                $class: 'ChoiceParameterDefinition',
                choices: "OK\n",
                name: 'result'
            ]
        ]
    )
    echo "This is a deploy step to ${checkInput}"
    sh "kubectl delete deployment ${deployment_name}${canary}"
}

def deployStable() {
    echo "检查你的正式pod"
    sh "kubectl apply -f ./deployment-stable.yaml --record"
}

def deployOperation() {
    def deployment_name = Config.settings.container_proj
    def stable = Config.settings.stable
    def operationInput = input(
        id: 'operationInput',
        message: 'need you do',
        parameters: [
            [
                $class: 'ChoiceParameterDefinition',
                choices: "Skip\nPause\nUndo",
                name: 'result'
            ]
        ]
    )
    if (operationInput == "Pause") {
        sh "kubectl rollout pause deployment ${deployment_name}${stable}"
    } else if (operationInput == "Undo"){
        sh "kubectl rollout undo deployment ${deployment_name}${stable}"
    } else if (operationInput == "Skip"){
        // deploy prod stuff
    }
}

def waitingStable() {
    def deployment_name = Config.settings.container_proj
    def stable = Config.settings.stable
    echo "等待pod部署完成"
    sh "kubectl rollout status deployment ${deployment_name}${stable}"
}

def finishStable() {
    def deployment_name = Config.settings.container_proj
    def stable = Config.settings.stable
    echo "等待pod部署完成"
    sh "kubectl describe deployment ${deployment_name}${stable}"
}

return this