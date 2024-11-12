# Basic Jenkins pipeline

This guide will help you set up Jenkins to automate the process of pulling a Git repository, building a Docker image, and pushing it to a Docker registry. In this example, we'll be using a Jenkins pipeline to achieve this workflow.

## Prerequisites

- Docker and Docker Compose are installed on your system.
- You have a GitHub repository that contains the code and Dockerfile for your application.
- You have a Docker Hub account for pushing Docker images.

## Steps

1. **Update Jenkinsfile**:
   Open the `Jenkinsfile` in your repository and change the Docker registry username and update the image tag according to your naming convention.

2. **Run Jenkins**:
   Start Jenkins using the following command:
   ```bash
   docker-compose -f docker-compose.jenkins.yml up
   ```
This will start the Jenkins server.

3. **Access Jenkins**:
   Open your web browser and navigate to `http://localhost:8080` to access the Jenkins web interface.

4. **Login to Jenkins**:
   You'll need the initial admin password to log in. You can find this password in the console where you started Jenkins or at `/var/jenkins_home/secrets/initialAdminPassword`. Follow the prompts to set up Jenkins.

5. **Install Plugins**:
   Choose the 'Install suggested plugins' option during the initial setup. This will install the necessary plugins for your Jenkins instance.

6. **Configure Pipeline**:
    - From the main dashboard, create a new pipeline item.
    - In the 'GitHub project' field, enter the URL of your GitHub repository.
    - Choose 'GitHub hook trigger for GITScm polling' in the build triggers section.
    - Select 'Pipeline script from SCM' in the pipeline section.
    - Choose 'Git' for SCM and enter your repository URL.
    - Authorize Jenkins to access your GitHub repository.
    - Set '*/master' as the branch to build.
    - Save the configuration.

7. **Create Docker Credentials**:
    - Create a new credential with the following configurations:
        - Kind: Secret text
        - Scope: Global
        - Secret: (Insert your Docker Hub password here)
        - ID: dockerhub-pwd

8. **Configure Tools**:
    - Navigate to 'Manage Jenkins' > 'Global Tool Configuration'
    - Add Maven in the Maven installation section with the name 'maven_tool'.
    - Similarly, add Docker in the Docker installation section.

9. **Build Pipeline**:
    - Go back to the main dashboard and open the pipeline you created.
    - Choose the 'Build Now' option to trigger the pipeline.
