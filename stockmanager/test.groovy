def mvnbuild(ENV) {
    if (ENV == 'mule') {
        script {
            def mvnHome = tool name: 'maven-3.9.5', type: 'Maven'
            def mvnCmd = "${mvnHome}/bin/mvn"
            sh "${mvnCmd} clean package"
            archiveJar()
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
