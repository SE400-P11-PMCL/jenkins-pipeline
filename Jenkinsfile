pipeline {
    agent any
    tools {
        maven 'maven_tool'
    }
    stages {
        stage('Checkout Code') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/SE400-P11-PMCL/jenkins-pipeline']])
            }
        }
        stage('Build Maven') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('SonarQube Analysis') {
            def mvn = tool 'maven_tool';
            withSonarQubeEnv() {
              sh "${mvn}/bin/mvn clean verify sonar:sonar -Dsonar.projectKey=cicd-se400"
            }
        }
        stage('Build docker image') {
            steps {
                sh 'docker build -t ducminh210503/cicd-se400 .'
            }
        }
        stage('Push image to Hub') {
            steps {
                withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd')]) {
                    sh 'docker login -u ducminh210503 -p ${dockerhubpwd}'
                }
                sh 'docker tag ducminh210503/cicd-se400 ducminh210503/cicd-se400'
                sh 'docker push ducminh210503/cicd-se400'
            }
        }
    }
}
