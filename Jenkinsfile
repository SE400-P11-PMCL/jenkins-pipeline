pipeline {
    agent any
    parameters {
        string(name: 'COMMIT_SHA', defaultValue: '', description: 'Commit SHA to checkout')
        booleanParam(name: 'ROLLBACK_ON_FAILURE', defaultValue: true, description: 'Rollback on failure')
    }
    environment {
        SONAR_TOKEN = credentials('sonartoken')
        PATH = "C:\\WINDOWS\\SYSTEM32;C:\Program Files\Java\jdk-19;C:\\Program Files\\Docker\\Docker\\resources\\bin;D:\\helm-v3.16.3-windows-amd64\\windows-amd64;D:\\trivy_0.58.1_windows-64bit;D:\\apache-jmeter-5.6.3\\apache-jmeter-5.6.3\\bin;${env.PATH}"
        KUBECONFIG = "C:\\Users\\Admin\\.kube\\config"
        DOCKER_IMAGE = "cicd-se400"
        HELM_CHART = "D:\\Workspace\\Reference\\cicd\\deploy"
        KUBERNETES_NAMESPACE_DEV = "dev"
        KUBERNETES_NAMESPACE_STAGING = "staging"
        KUBERNETES_NAMESPACE_PROD = "prod"
    }
    tools {
        maven 'maven_tool'
    }
    options {
        skipDefaultCheckout(true)
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    stages {
        stage("Cleanup Workspace") {
            steps {
                cleanWs()
            }
        }
        stage('Checkout Code') {
            steps {
                script {
                    if (params.COMMIT_SHA) {
                        echo "Checking out commit ${params.COMMIT_SHA}"
                        checkout([$class: 'GitSCM',
                                  branches: [[name: params.COMMIT_SHA]],
                                  userRemoteConfigs: [[url: 'https://github.com/SE400-P11-PMCL/jenkins-pipeline.git']]
                        ])
                    } else {
                        echo "Checking out branch ${env.BRANCH_NAME}"
                        checkout scm
                    }

                    env.GIT_BRANCH_NAME = env.BRANCH_NAME ?: bat(
                        script: 'git rev-parse --abbrev-ref HEAD',
                        returnStdout: true
                    ).trim()
                }
            }
        }
        stage('Build Maven') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }
        stage('Unit Test') {
            steps {
                bat 'mvn test '
            }
        }
        stage('Integration Test') {
            when { anyOf { branch 'staging' } }
            steps {
                bat 'mvn verify'
            }
        }
        stage('Performance Test') {
            when { anyOf { branch 'staging' } }
            steps {
                bat 'jmeter -n -t HTTPRequest.jmx'
            }
        }
        stage('SonarQube Analysis') {
            when {
                not {
                    branch pattern: "feature/.*", comparator: "REGEXP"
                }
            }
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
            when {
                not {
                    branch pattern: "feature/.*", comparator: "REGEXP"
                }
            }
            steps {
                script {
                    try {
                        bat """
                            docker build -t ${DOCKER_IMAGE} .
                        """
                    } catch (Exception e) {
                        echo "Error: ${e}"
                        currentBuild.result = 'FAILURE'
                        error("Failed to build Docker image: ${e.message}")
                    }
                }
            }
        }
        stage('Scan Docker Image') {
            when {
                not {
                    branch pattern: "feature/.*", comparator: "REGEXP"
                }
            }
            steps {
                script {
                    try {
                        bat """
                            trivy image --severity HIGH,CRITICAL --no-progress --format table -o trivy-report.html ${DOCKER_IMAGE}
                        """
                    } catch (Exception e) {
                        echo "Error: ${e}"
                        currentBuild.result = 'FAILURE'
                        error("Failed to scan Docker image: ${e.message}")
                    }
                }
            }
        }
        stage('Push Docker Image') {
            when {
                not {
                    branch pattern: "feature/.*", comparator: "REGEXP"
                }
            }
            steps {
                script {
                    env.IMAGE_TAG = "${env.GIT_BRANCH_NAME}-${env.BUILD_NUMBER}"
                    try {
                        withCredentials([string(credentialsId: 'dockerhubpwd', variable: 'dockerhubpwd')]) {
                            bat 'docker login -u ducminh210503 -p %dockerhubpwd%'
                        }
                        bat """docker tag cicd-se400 ducminh210503/${DOCKER_IMAGE}:${IMAGE_TAG}"""
                        bat """docker push ducminh210503/${DOCKER_IMAGE}:${IMAGE_TAG}"""

                        bat """docker rmi ${DOCKER_IMAGE} -f"""
                        bat """docker rmi ducminh210503/${DOCKER_IMAGE}:${IMAGE_TAG} -f"""
                    }
                    catch (Exception e) {
                        echo "Error: ${e}"
                        currentBuild.result = 'FAILURE'
                        error("Failed to push Docker image: ${e.message}")
                    }
                }
            }
        }
        stage('Deploy to Staging Kubernetes') {
            when {
                expression {
                    env.BRANCH_NAME ==~ /(develop)/
                }
            }
            steps {
                script {
                    def namespace = getKubernetesNamespace(env.GIT_BRANCH_NAME)
                    echo "Deploying to Kubernetes Namespace: ${namespace}"

                    try {
                        bat """
                            helm upgrade --install ${DOCKER_IMAGE} ${HELM_CHART} ^
                                --namespace staging ^
                                --values ./deploy/values-staging.yaml ^
                                --set image.tag=${IMAGE_TAG}
                        """
                    } catch (Exception e) {
                        if (params.ROLLBACK_ON_FAILURE) {
                            echo "Deployment failed, rolling back..."
                            bat "helm rollback cicd-se400 --namespace ${namespace}"
                        }
                        error("Deployment to ${namespace} failed: ${e.message}")
                    }
                }
            }
        }
        stage('Deploy for production') {
            when {
                branch 'staging'
            }
            steps {
                input message: 'Finished testing the web site? (Click "Proceed" to continue)'
            }
        }
        stage('Deploy to Production Kubernetes') {
            when {
                expression {
                    env.BRANCH_NAME ==~ /(staging)/
                }
            }
            steps {
                script {
                    def namespace = getKubernetesNamespace(env.GIT_BRANCH_NAME)
                    echo "Deploying to Kubernetes Namespace: ${namespace}"

                    try {
                        bat """
                            helm upgrade --install ${DOCKER_IMAGE} ${HELM_CHART} ^
                                --namespace prod ^
                                --values ./deploy/values-prod.yaml ^
                                --set image.tag=${IMAGE_TAG}
                        """
                    } catch (Exception e) {
                        if (params.ROLLBACK_ON_FAILURE) {
                            echo "Deployment failed, rolling back..."
                            bat "helm rollback your-app --namespace ${namespace}"
                        }
                        error("Deployment to ${namespace} failed: ${e.message}")
                    }
                }
            }
        }

//         stage('Deploy ELK Stack') {
//             steps {
//                 sh 'docker-compose -f docker-compose.local.yml up -d'
//             }
//         }
    }
    post {
        always {
            script {
                if (!(env.BRANCH_NAME ==~ /feature\/.*/)) {
                    publishHTML(target: [
                        reportName: 'Trivy Report',
                        reportDir: '.',
                        reportFiles: 'trivy-report.html',
                        alwaysLinkToLastBuild: true,
                        keepAll: true
                    ])
                }
            }
            cleanWs(cleanWhenNotBuilt: false,
                                deleteDirs: true,
                                disableDeferredWipeout: true,
                                notFailBuild: true,
                                patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                                           [pattern: '.propsfile', type: 'EXCLUDE']])
            emailext(
                subject: "Build ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${currentBuild.result}",
                body: """
                Job: ${env.JOB_NAME} \n
                Build Number: ${env.BUILD_NUMBER} \n
                Build Status: ${currentBuild.result} \n
                Build URL: ${env.BUILD_URL} \n
                """,
                to: "vuducminh210503@gmail.com"
            )
        }
    }
}

def getKubernetesNamespace(branchName) {
    if (branchName.startsWith("develop")) {
        return KUBERNETES_NAMESPACE_DEV
    } else if (branchName.startsWith("staging")) {
        return KUBERNETES_NAMESPACE_STAGING
    } else if (branchName == "main") {
        return KUBERNETES_NAMESPACE_PROD
    } else {
        error("Branch ${branchName} is not allowed for deployment")
    }
}