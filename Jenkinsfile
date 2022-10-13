pipeline {

  agent any

  options {
    // Only keep the 5 most recent builds
    buildDiscarder(logRotator(numToKeepStr: '500'))
  }

  stages {

    stage('Clean up workspace') {
      steps {
        deleteDir()
      }
    }

    stage('SCM') {
      steps {
        git 'https://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@github.com/DiegoPinzon20/ohi-salud-sura-automation.git'
      }
    }

    stage('Gradle Build Artifact And Run Test') {
      steps {
        script {
          def gradleCommandBasic = './gradlew clean build test --tests "ApiSupervisionQueryDiagnosticRunner" aggregate'
          if (isUnix()) {
            sh gradleCommandBasic
          } else {
            bat gradleCommandBasic
          }
        }
      }
    }
  }

  post {
    always {
      publishHTML(
        [allowMissing: false,
          alwaysLinkToLastBuild: true,
          keepAll: true,
          reportDir: 'target/site/serenity',
          reportFiles: 'index.html',
          reportName: 'HTML Report'
        ]
      )
      archiveArtifacts artifacts: "build/libs/*.jar",
        allowEmptyArchive: true,
        fingerprint: true
      junit "build/test-results/test/*.xml"
    }

    failure {
      mail to: 'diego.pizon@sofka.com.co',
        subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
        body: "Something is wrong with ${env.BUILD_URL}"
    }
  }
}
