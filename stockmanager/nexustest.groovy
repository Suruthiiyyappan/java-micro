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
    // Read POM xml file using 'readMavenPom' step
    def pom = readMavenPom file: "pom.xml"

    // Find built artifact under the target folder
    def filesByGlob = findFiles(glob: "target/*.jar")

    // Print some info from the artifact found
    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"

    // Extract the path from the File found
    def artifactPath = filesByGlob[0].path

    // Assign to a boolean response verifying if the artifact exists
    def artifactExists = fileExists artifactPath

    if (artifactExists) {
        echo "*** File: ${artifactPath}, group: uk.co.danielbryant.djshopping, packaging: jar, version: 0.0.1-SNAPSHOT"

        nexusArtifactUploader(
            nexusVersion: "nexus3",
            protocol: "http",
            nexusUrl: "http://3.99.33.174:8081/repository/maven-snapshots",
            groupId: "uk.co.danielbryant.djshopping",
            version: "0.0.1-SNAPSHOT",
            repository: "maven-snapshots",
            credentialsId: "nexus3",
            artifacts: [
                [
                    artifactId: "stockmanager",
                    classifier: '',
                    file: "target/stockmanager-0.0.1-SNAPSHOT.jar",
                    type: "jar"
                ]
            ]
        )
    }
}

return this

// def nexus() {
//     def mvnCmd = "/opt/apache-maven-3.9.5/bin/mvn"

//     // Use withEnv to set the PATH environment variable
//     withEnv(["PATH+MAVEN=/opt/apache-maven-3.9.5/bin"]) {
//         withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId:'nexus3', usernameVariable: 'nexus3_Username', passwordVariable: 'nexus3_Password']]) {
//             sh """
//                 ${mvnCmd} deploy:deploy-file \
//                 -Dfile=target/stockmanager-0.0.1-SNAPSHOT.jar \
//                 -DartifactId=stockmanager \
//                 -Dversion=0.0.1-SNAPSHOT \
//                 -Dpackaging=jar \
//                 -DrepositoryId=maven-snapshots \
//                 -Durl=http://3.99.33.174:8081/repository/maven-snapshots/ \
//                 -DgroupId=uk.co.danielbryant.djshopping \
//                 -Dusername=$nexus3_Username \
//                 -Dpassword=$nexus3_Password
//             """
//         }
//     }
// }

