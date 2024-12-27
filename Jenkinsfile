pipeline {
    agent any
    environment {
        SONAR_TOKEN = credentials('sonartoken')
    }
    tools {
        maven 'maven_tool'
    }
    stages {
        stage('Build Maven') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('SonarQube Analysis') {
            steps {
                script {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=cicd-se400 \
                        -Dsonar.host.url=http://localhost:9001 \
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
                withCredentials([string(credentialsId: 'dockerhubpwd', variable: 'dockerhubpwd')]) {
                    sh 'docker login -u ducminh210503 -p ${dockerhubpwd}'
                }
                sh 'docker tag ducminh210503/cicd-se400 ducminh210503/cicd-se400'
                sh 'docker push ducminh210503/cicd-se400'
            }
        }
        stage('Deliver for development') {
            when {
                branch 'development'
            }
            steps {
                sh './jenkins/scripts/deliver-for-development.sh'
                input message: 'Finished using the web site? (Click "Proceed" to continue)'
                sh './jenkins/scripts/kill.sh'
            }
        }
        stage('Deploy for production') {
            when {
                branch 'production'
            }
            steps {
                sh './jenkins/scripts/deploy-for-production.sh'
                input message: 'Finished using the web site? (Click "Proceed" to continue)'
                sh './jenkins/scripts/kill.sh'
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
