def mvnbuild() {
    sh "mvn clean package"
}

def sonar() {
    withSonarQubeEnv(credentialsId: 'sonar', installationName: 'sonarqube') {
        sh "mvn clean verify sonar:sonar -Dsonar.host.url=$sonar_url -Dsonar.projectName=$sonar_name"
    }
}

def qualitygate() {
    def qg = waitForQualityGate()
    if (qg.status != 'OK') {
        error "Quality gate not OK: ${qg.status}"
    }
}

def approval() {
    def approvalEmail = mail(
        subject: "approve for ${env.BUILD_NUMBER}",
        body: """${env.JOB_URL}/${env.BUILD_NUMBER}/input/">Approve / Reject </a> this build to import in the Dev Fabric.. <br>""",
        mimeType: 'text/html',
        to: "suruthi.iyappan@gmail.com"
    )

    timeout(time: 1, unit: 'HOURS') {
        def approvalInput = input(
            id: 'approvalInput',
            message: 'Approve or reject the for DEV',
            parameters: [choice(choices: ['Approve', 'Reject'], description: 'Choose to approve or reject', name: 'approvalChoice')],
            submitter: 'admin'
        )

        // Get the selected value from the input
        def approvalChoice = approvalInput.approvalChoice

        if (approvalChoice == 'Approve') {
            sh "sudo aws s3 cp stockmanager-0.0.1-SNAPSHOT.jar s3://suruthi1/artifacts/ --acl bucket-owner-full-control"
        } else {
            error('Deployment aborted by user')
        }
    }
}
