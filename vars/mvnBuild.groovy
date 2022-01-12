def Build(){

        sh 'cat /home/jenkins/settings/settings.xml'
        sh 'mvn clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -gs /home/jenkins/settings/settings.xml'

}