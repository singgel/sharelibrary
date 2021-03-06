def config() {
    defaultSettings()
}


def defaultSettings(){
    Config.settings = [
            harbor_domain     : 'xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com',
            git_repo          : "${env.git_repo}",
            git_branchName    : "${env.git_branchName}",
            git_credentialsId : "${env.git_credentialsId}",
            harbor_credentialsId : "${env.harbor_credentialsId}",
            container_env     : "${env.container_env}",
            container_proj    : "${env.container_proj}",
            container_region  : "${env.container_region}",
            container_host    : "${env.container_host}",
            build_zip_path    : "${env.build_zip_path}",
            build_zip_file    : "${env.build_zip_file}",
            build_unzip_dir   : "${env.build_unzip_dir}",
            repository_group  : '',
            git_version       : '',
            image_version     : '',
            webhook           : "${env.webhook}",
            deploy_user       : "",
            ready_time        : "${env.ready_time}",
            max_surge         : "${env.max_surge}",
            replicas_number   : "${env.replicas_number}",
            canary            : "-canary",
            stable            : ""
    ]
}



