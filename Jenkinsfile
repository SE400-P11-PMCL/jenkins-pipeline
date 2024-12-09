pipeline {
    agent any
    environment {
        SONAR_TOKEN = credentials('sonartoken')
    }
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
            steps {
                script {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=cicd-se400 \
                        -Dsonar.host.url=http://54.255.246.62:9000 \
                        -Dsonar.login=${SONAR_TOKEN}
                    """
                }
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
