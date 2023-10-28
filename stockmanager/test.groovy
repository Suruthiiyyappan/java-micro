def mvnbuild(ENV) {
    if (ENV == 'mule') {
        script {
            // def mvnHome = tool name: 'Maven-Name', type: 'Maven'
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

def sonar() {
    def mvnCmd = "/opt/apache-maven-3.9.5/bin/mvn"

    // Use withEnv to set the PATH environment variable
    withEnv(["PATH+MAVEN=/opt/apache-maven-3.9.5/bin"]) {
        withSonarQubeEnv(credentialsId: 'sonar', installationName: 'sonarqube') {
            def sonar_url = 'http://3.97.203.42:9000/'
            def sonar_name = 'goov'

            sh """${mvnCmd} clean verify sonar:sonar \\
            -Dsonar.host.url=${sonar_url} \\
            -Dsonar.projectName=ss"""
        }
    }
}


return this
