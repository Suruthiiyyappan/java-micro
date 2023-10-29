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
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId:'nexus3', usernameVariable: 'nexus3_Username', passwordVariable: 'nexus3_Password']]) {
            sh """
                ${mvnCmd} deploy:deploy-file \
                -Dfile=target/stockmanager-0.0.1-SNAPSHOT.jar \
                -DartifactId=stockmanager \
                -Dversion=0.0.1-SNAPSHOT \
                -Dpackaging=jar \
                -DrepositoryId=maven-snapshots \
                -Durl=http://3.99.33.174:8081/repository/maven-snapshots/ \
                -DgroupId=uk.co.danielbryant.djshopping \
                -Dusername=$nexus3_Username \
                -Dpassword=$nexus3_Password
            """
        }
    }
}


return this
