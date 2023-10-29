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

def nexus() {
    def mvnCmd = "/opt/apache-maven-3.9.5/bin/mvn"

    // Use withEnv to set the PATH environment variable
    withEnv(["PATH+MAVEN=/opt/apache-maven-3.9.5/bin"]) {
        withSonarQubeEnv(credentialsId: 'nexus3') {
            nexusArtifactUploader(
                artifacts: [
                    [
                        artifactId: 'stockmanager',
                        classifier: '',
                        file: 'target/stockmanager-0.0.1-SNAPSHOT.jar',
                        type: 'jar'
                    ]
                ],
                credentialsId: 'nexus3',
                groupId: 'uk.co.danielbryant.djshopping',
                nexusUrl: 'http://3.99.33.174:8081',
                nexusVersion: 'nexus3',
                protocol: 'http',
                repository: 'http://3.99.33.174:8081/repository/app1-release/',
                version: '0.0.1-SNAPSHOT'
            )
        }
    }
}
    

return this
