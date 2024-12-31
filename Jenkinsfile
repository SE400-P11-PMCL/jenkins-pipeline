pipeline {
    agent any
    parameters {
        string(name: 'COMMIT_SHA', defaultValue: '', description: 'Commit SHA to checkout')
        booleanParam(name: 'ROLLBACK_ON_FAILURE', defaultValue: true, description: 'Rollback on failure')
    }
    environment {
        SONAR_TOKEN = credentials('sonartoken')
        PATH = "C:\\WINDOWS\\SYSTEM32;C:\\Program Files\\Docker\\Docker\\resources\\bin;D:\\helm-v3.16.3-windows-amd64\\windows-amd64;${env.PATH}"
        KUBECONFIG = "C:\\Users\\Admin\\.kube\\config"
        DOCKER_IMAGE = "cicd-se400"
        HELM_CHART = "D:\\Workspace\\Reference\\cicd\\deploy"
        KUBERNETES_NAMESPACE_DEV = "dev"
        KUBERNETES_NAMESPACE_STAGING = "staging"
        KUBERNETES_NAMESPACE_PROD = "prod"
        TRIVY_HOME="D:\\trivy_0.58.1_windows-64bit"
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
            steps {
                script {
                    try {
                        bat """
                            trivy image --exit-code 0 --severity HIGH,CRITICAL --no-progress ${DOCKER_IMAGE}
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
        stage('Deploy to Kubernetes') {
            when {
                expression {
                    env.BRANCH_NAME ==~ /(feature|staging|main)/
                }
            }
            steps {
                script {
                    def namespace = getKubernetesNamespace(env.GIT_BRANCH_NAME)
                    echo "Deploying to Kubernetes Namespace: ${namespace}"

                    try {
                        bat """
                            helm upgrade --install ${DOCKER_IMAGE} ${HELM_CHART} ^
                                --namespace ${namespace} ^
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