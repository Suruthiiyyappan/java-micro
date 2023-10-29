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
    pom = readMavenPom file: "pom.xml"

    // Find built artifact under the target folder
    filesByGlob = findFiles(glob: "target/*.jar")

    // Print some info from the artifact found
    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"

    // Extract the path from the File found
    artifactPath = filesByGlob[0].path

    // Assign to a boolean response verifying if the artifact exists
    artifactExists = fileExists artifactPath

    if (artifactExists) {
        echo "*** File: ${artifactPath}, group: uk.co.danielbryant.djshopping, packaging: jar, version: 0.0.1-SNAPSHOT"

        nexusArtifactUploader(
            nexusVersion: "nexus3",
            protocol: "http",
            nexusUrl: "http://3.99.33.174:8081/repository/maven-snapshots/",
            groupId: "uk.co.danielbryant.djshopping",
            version: "${BUILD_NUMBER}",
            repository: "maven-snapshots",
            credentialsId: "maven3",
            artifacts: [
                // Artifact generated such as .jar, .ear, and .war files.
                [
                    artifactId: "stockmanager", // Wrap artifactId in double quotes
                    classifier: '',
                    file: "target/stockmanager-0.0.1-SNAPSHOT.jar", // Wrap file path in double quotes
                    type: "jar" // Wrap artifact type in double quotes
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

