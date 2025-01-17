# Jenkins CI/CD Pipeline

## Overview
This repository contains a robust CI/CD pipeline implemented using Jenkins, integrated with modern DevOps tools and best practices. The pipeline is designed to support multi-environment deployments leveraging Git Flow and provides quality assurance through static code analysis, container security scanning, load testing, and more.

## Key Features
- **Version Control**: Git with Git Flow branching strategy for streamlined development and release processes.
- **Build Automation**: Maven for compiling, testing, and packaging Java applications.
- **Containerization**: Docker for creating lightweight, portable, and consistent containers.
- **Security Scanning**: Trivy for container image vulnerability scanning.
- **Performance Testing**: JMeter for load and performance testing.
- **Code Quality**: SonarQube for static code analysis and ensuring code quality standards.
- **Orchestration**: Helm for managing Kubernetes deployments.
- **Local Kubernetes Environment**: Minikube for testing and validating deployments in a local Kubernetes cluster.

## Tools and Technologies
- **Source Code Management**: Git (Git Flow)
- **CI/CD Orchestration**: Jenkins
- **Build Tool**: Maven
- **Containerization**: Docker
- **Container Security**: Trivy
- **Load Testing**: JMeter
- **Static Code Analysis**: SonarQube
- **Kubernetes Deployment**: Helm
- **Local Kubernetes**: Minikube

---

## Prerequisites
Ensure the following are installed and configured on your system:
- **Jenkins** with necessary plugins:
  - Git Plugin
  - Docker Plugin
  - Kubernetes Plugin
  - SonarQube Scanner Plugin
  - Pipeline Plugin
- **Docker**
- **Trivy**
- **Apache JMeter**
- **SonarQube Server**
- **Helm**
- **Minikube**
- **Maven**

---

## Pipeline Workflow

### 1. Branching Strategy
- **Git Flow**:
  - `main`: Production-ready code.
  - `develop`: Integration branch for feature development.
  - `feature/*`: Feature-specific branches.
  - `release/*`: Release preparation branches.
  - `hotfix/*`: Quick patches for production.

### 2. Pipeline Stages
#### a. **Code Checkout**
- Clone the repository from GitHub.
- Switch to the appropriate branch based on the Git Flow model.

#### b. **Build and Unit Testing**
- Use Maven to compile the code and run unit tests.
- Generate build artifacts (e.g., JAR files).

#### c. **Static Code Analysis**
- Run SonarQube to analyze code quality and report vulnerabilities.

#### d. **Containerization**
- Build Docker images for the application.
- Tag the images using the branch name and version.

#### e. **Security Scanning**
- Scan Docker images for vulnerabilities using Trivy.

#### f. **Load Testing**
- Execute JMeter scripts to test the application’s performance under load.

#### g. **Kubernetes Deployment**
- Deploy the application to a local Kubernetes cluster using Helm and Minikube.
- Test the deployment in multiple environments (e.g., dev, staging, prod).

### 3. Multi-Environment Deployment
- Utilize Git Flow branches to deploy to different environments:
  - `develop` -> Development environment.
  - `staging` -> Staging environment.
  - `main` -> Production environment.

## Local Setup and Testing
1. Start Minikube:
   ```bash
   minikube start
   ```

2. Deploy SonarQube locally:
   ```bash
   helm install sonarqube sonarqube/sonarqube
   ```

3. Test the pipeline locally using:
   ```bash
   jenkins-cli build <job-name>
   ```

---

## Support
For any issues or questions, please raise an issue in this repository.

