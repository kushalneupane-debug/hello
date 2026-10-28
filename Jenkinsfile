pipeline {
    agent any

    environment {
        PATH+DOCKER    = "/usr/local/bin:/opt/homebrew/bin:/Applications/Docker.app/Contents/Resources/bin"
        DOCKERHUB_REPO = 'neup2666/hello'
        IMAGE_TAG      = new Date().format('yyyyMMdd-HHmm')
        DOCKERHUB_CRED = 'dockerhub-creds'
        EC2_HOST       = 'ec2-3-136-170-216.us-east-2.compute.amazonaws.com'
        EC2_USER       = 'ubuntu'
        EC2_CREDS      = 'ec2-creds'
        APP_PORT       = '8080'
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build Docker image') {
            steps {
                sh "docker version"
                sh "docker build --platform linux/amd64 -t ${env.DOCKERHUB_REPO}:${env.IMAGE_TAG} ."
            }
        }

        stage('Push Docker image') {
            steps {
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CRED,
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh """
                        echo "\$DH_PASS" | docker login -u "\$DH_USER" --password-stdin
                        docker push ${env.DOCKERHUB_REPO}:${env.IMAGE_TAG}
                    """
                }
            }
        }

        stage('Deploy on EC2 (pull & run)') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: env.EC2_CREDS,
                                                   keyFileVariable: 'EC2_KEY')]) {
                    sh """
                        ssh -T -o BatchMode=yes -o ConnectTimeout=15 -o StrictHostKeyChecking=no \
                            -o IdentitiesOnly=yes -i "\$EC2_KEY" ${env.EC2_USER}@${env.EC2_HOST} \
                            "sudo docker pull ${env.DOCKERHUB_REPO}:${env.IMAGE_TAG} && \
                             (sudo docker rm -f hello || true) && \
                             (sudo docker ps -q --filter publish=9090 | xargs -r sudo docker rm -f) && \
                             sudo docker run -d --name hello --restart unless-stopped \
                               -e JAVA_TOOL_OPTIONS='-Xmx128m -Xms64m' \
                               -p 9090:${env.APP_PORT} ${env.DOCKERHUB_REPO}:${env.IMAGE_TAG}"
                    """
                }
            }
        }
    }

    post {
        always  { echo "Pipeline finished." }
        success { echo "Deployed: http://${env.EC2_HOST}:9090/" }
    }
}
