import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


def info(deploy_status) {
    def project_name = Config.settings.container_proj
    def deploy_env = Config.settings.container_env
    def deploy_user = Config.settings.deploy_user
    def deploy_date = getCurrentDate()
    def webhook = Config.settings.webhook

    String json = libraryResource("deploy_webhook_content")

    json = json.replaceAll("\\{\\{project_name}}", "$project_name")
    json = json.replaceAll("\\{\\{deploy_env}}", "$deploy_env")
    json = json.replaceAll("\\{\\{deploy_status}}", "$deploy_status")
    json = json.replaceAll("\\{\\{deploy_user}}", "$deploy_user")
    json = json.replaceAll("\\{\\{deploy_date}}", "$deploy_date")

    sh "echo '$json'"

    sh "curl -X POST -H \"Content-Type: application/json\" -d '$json' $webhook"
}

static String getCurrentDate() {
    Instant now = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8))
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return LocalDateTime.ofInstant(now, ZoneId.systemDefault()).format(dateTimeFormatter)
}

