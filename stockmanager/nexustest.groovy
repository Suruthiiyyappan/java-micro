def mvnbuild(ENV) {
    if (ENV == 'mule') {
        script {
            def mvnCmd = "/opt/apache-maven-3.9.5/bin/mvn"

            // Use withEnv to set the PATH environment variable
            withEnv(["PATH+MAVEN=/opt/apache-maven-3.9.5/bin"]) {
                sh "${mvnCmd} clean package"
                archiveJar()
            }
        }
    } else {
        echo 'not build'
    }
}

def archiveJar() {
    archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
}

def nexus() {
    def mvnCmd = "/opt/apache-maven-3.9.5/bin/mvn"

    // Use withEnv to set the PATH environment variable
    withEnv(["PATH+MAVEN=/opt/apache-maven-3.9.5/bin"]) {
        sh "${mvnCmd} deploy:deploy-file -Durl=http://3.99.33.174:8081 -Dfile=target/stockmanager-0.0.1-SNAPSHOT.jar -DgroupId=com.example -DartifactId=uk.co.danielbryant.djshopping -Dpackaging=jar -Dversion=0.0.1-SNAPSHOT -DrepositoryId=http://3.99.33.174:8081/repository/app1-release/"
    }
}    

return this
