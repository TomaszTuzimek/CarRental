pipeline {
  agent any
  stages {
    stage('Checkout Git') {
      steps {
        git(url: 'https://github.com/TomaszTuzimek/CarRental', changelog: true, poll: true, branch: 'master')
      }
    }

    stage('Confirmation') {
      parallel {
        stage('error') {
          steps {
            echo 'All OK'
          }
        }

        stage('Install') {
          steps {
            sh 'mvn clean install'
          }
        }

      }
    }

    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }

    stage('Run') {
      steps {
        sh 'java -jar target/CarRental-1.jar'
      }
    }

  }
}