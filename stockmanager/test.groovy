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
    sh "mvn clean verify sonar:sonar \
    -Dsonar.host.url=$sonar_url \
    -Dsonar.projectName=ss"
    }

return this
