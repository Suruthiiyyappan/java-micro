pipeline {
    agent any
    environment {
        DOCKER_IMAGE_TAG = "suruthi:${BUILD_ID}"
        HELM_RELEASE_NAME = 'javaapplication'
        HELM_PACKAGE_NAME = "suruthi-0.${BUILD_ID}.0.tgz"
        HELM_PULL = "suruthi"
    }  
    tools {
        maven 'maven'
    }
    stages {
        stage('Checkout SCM') {
            steps {
                git branch: 'main', credentialsId: 'github-credentials', url: 'https://github.com/Suruthiiyyappan/java-micro.git'
            }
        }
        stage('Build') {
            steps {
                dir('stockmanager') {
                    sh "mvn clean package"
                }
            }
        }
        // stage('push artifacts to S3') {
        //     steps {
        //         dir('stockmanager/target') {
        //             sh "sudo aws s3 cp stockmanager-0.0.1-SNAPSHOT.jar s3://suruthi/artifacts/ --acl bucket-owner-full-control"
        //         }
        //     }
        // }
        stage('Approve for DEV') {
                steps {
                    script {
                        def approvalEmail = mail(
                            subject: "approve for ${env.BUILD_NUMBER}",
                        body: """http://3.98.164.66:8080/job/work1/${BUILD_ID}/input/">Approve / Reject </a> this build to import in the Dev Fabric.. <br>""",              
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
                            if (approvalInput == 'Approve') {
                                    echo "approved"
                                
                            } else {
                                error('Deployment aborted by user')
                            }
                        }
                    }
                }
            }


    }
}
