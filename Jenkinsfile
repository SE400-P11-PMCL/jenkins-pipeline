pipeline {
    agent any
    environment {
        SONAR_TOKEN = credentials('sonartoken')
        PATH = "C:\\WINDOWS\\SYSTEM32;C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
        KUBECONFIG = "C:\\Users\\Admin\\.kube\\config"
    }
    tools {
        maven 'maven_tool'
    }
    options {
        skipDefaultCheckout(true)
    }
    stages {
        stage("Cleanup Workspace") {
            steps {
                cleanWs()
            }
        }
        stage('Checkout Code') {
            steps {
                checkout scmGit(branches: [[name: '*/main']],
                    extensions: [],
                    userRemoteConfigs: [[url: 'https://github.com/SE400-P11-PMCL/jenkins-pipeline.git']])
            }
        }
        stage('Verify Kubernetes') {
            steps {
                bat 'kubectl get pods'
            }
        }
        stage('Build Maven') {
            steps {
                bat 'mvn clean package -DskipTests'
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
        always {
            cleanWs(cleanWhenNotBuilt: false,
                                deleteDirs: true,
                                disableDeferredWipeout: true,
                                notFailBuild: true,
                                patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                                           [pattern: '.propsfile', type: 'EXCLUDE']])
            emailext(
                subject: "Build ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${currentBuild.result}",
                body: """
                Job: ${env.JOB_NAME}
                Build Number: ${env.BUILD_NUMBER}
                Build Status: ${currentBuild.result}
                Build URL: ${env.BUILD_URL}
                """,
                to: "vuducminh210503@gmail.com"
            )
        }
    }
}
