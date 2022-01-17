/* Config.groovy
   ##################################################
   # Created by Lin Ru at 2018.10.01 22:00          #
   #                                                #
   # A Part of the Project jenkins-library          #
   #  https://github.com/Statemood/jenkins-library  #
   ##################################################
*/

// CI Config
class Config implements Serializable {
    static Map settings = [:]

    def defaultSettings(){
        Config.settings = [
                harbor_domain    : 'xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com'

        ]
    }
}