pipeline {
  agent any
  stages {
    stage('Test') {
      steps {
        powershell 'mvn clean'
        powershell 'mvn test'
      }
    }

  }
}