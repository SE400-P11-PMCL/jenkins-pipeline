pipeline {
    agent any
    environment {
        SONAR_TOKEN = credentials('sonartoken')
        PATH = "C:\\WINDOWS\\SYSTEM32"
    }
    tools {
        maven 'maven_tool'
    }
    stages {
        stage('Checkout Code') {
            steps {
                checkout scmGit(branches: [[name: '*/main']],
                    extensions: [],
                    userRemoteConfigs: [[url: 'https://github.com/SE400-P11-PMCL/jenkins-pipeline.git']])
            }
        }
        stage('Build Maven') {
            steps {
                bat 'mvn clean package'
            }
        }
        stage('Unit Test') {
            steps {
                bat 'mvn test'
            }
        }
        stage('SonarQube Analysis') {
            steps {
                script {
                    bat """
                        mvn sonar:sonar ^
                        -Dsonar.projectKey=cicd-se400 ^
                        -Dsonar.host.url=http://localhost:9001 ^
                        -Dsonar.login=%SONAR_TOKEN%
                    """
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                bat 'docker build -t ducminh210503/cicd-se400 .'
            }
        }
        stage('Push Docker Image') {
            steps {
                withCredentials([string(credentialsId: 'dockerhubpwd', variable: 'dockerhubpwd')]) {
                    bat 'docker login -u ducminh210503 -p %dockerhubpwd%'
                }
                bat 'docker tag ducminh210503/cicd-se400 ducminh210503/cicd-se400'
                bat 'docker push ducminh210503/cicd-se400'
            }
        }
    }
    post {
        success {
            emailext(
                subject: 'Pipeline Succeeded',
                body: 'Pipeline has succeeded.',
                to: 'vuducminh210503@gmail.com'
            )
        }
        failure {
            emailext(
                subject: 'Pipeline Failed',
                body: 'Pipeline has failed.',
                to: 'vuducminh210503@gmail.com'
            )
        }
    }
}
