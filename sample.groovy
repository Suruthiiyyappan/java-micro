def mvnbuild() {
    try {
        sh "mvn clean package"
    } catch (Exception e) {
        currentBuild.result = 'FAILURE'
        error "Maven build failed: ${e.message}"
    }
}

def sonar() {
    try {
        withSonarQubeEnv(credentialsId: 'sonar', installationName: 'sonarqube') {
            sh "mvn clean verify sonar:sonar -Dsonar.host.url=$sonar_url -Dsonar.projectName=$sonar_name"
        }
    } catch (Exception e) {
        currentBuild.result = 'FAILURE'
        error "SonarQube analysis failed: ${e.message}"
    }
}

def qualitygate() {
    def qg = waitForQualityGate()
    if (qg.status != 'OK') {
        currentBuild.result = 'FAILURE'
        error "Quality gate not OK: ${qg.status}"
    }
}

def approval() {
    def approvalEmail = mail(
        subject: "Approve for Build ${env.BUILD_NUMBER}",
        body: """${env.JOB_URL}/${env.BUILD_NUMBER}/input/">Approve / Reject </a> this build to import in the Dev Fabric.. <br>""",
        mimeType: 'text/html',
        to: "suruthi.iyappan@gmail.com"
    )

    timeout(time: 1, unit: 'HOURS') {
        def approvalInput = input(
            id: 'approvalInput',
            message: 'Approve or reject the deployment to DEV',
            parameters: [choice(choices: ['Approve', 'Reject'], description: 'Choose to approve or reject', name: 'approvalChoice')],
            submitter: 'admin'
        )

        def approvalChoice = approvalInput.approvalChoice

        if (approvalChoice == 'Approve') {
            try {
                echo "app"
            } catch (Exception e) {
                currentBuild.result = 'FAILURE'
                error "app failed: ${e.message}"
            }
        } else {
            error('Deployment aborted by user')
        }
    }
}
