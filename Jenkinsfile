pipeline {
    agent any
        tools {
            maven 'maven_tool'
        }
        stages {
            stage('Build Maven') {
                steps {
                    checkout scmGit(branches: [[name: '*/feat/helper-user']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/ttqteo/iot-cloud-helper-spring']])
                    sh 'mvn clean install'
                }
            }
        }
    }
}
