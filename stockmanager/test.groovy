def mvnbuild(ENV) {
    if (ENV == 'mule') {
        sh 'mvn clean package'
        archiveJar()
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
