pipeline {
  agent any
  stages {
    stage('Checkout Git') {
      steps {
        git(url: 'https://github.com/TomaszTuzimek/CarRental', changelog: true, poll: true, branch: 'master')
      }
    }

    stage('') {
      steps {
        echo 'All OK'
      }
    }

  }
}