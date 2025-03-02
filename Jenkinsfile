pipeline {
  agent any
  stages {
    stage('Test') {
      steps {
        sh 'mvn clean'
        sh 'mvn test'
      }
    }

  }
}