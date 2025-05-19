pipeline {

    agent any

    environment {
        DOCKER_PAT = credentials('docker-pat')
        SSH_KEY = credentials('ec2-ssh-key')
        DOCKER_FRONTEND_IMG = 'tankaus/frontend'
        DOCKER_BACKEND_IMG =  'tankaus/backend'
    }

    stages {
        
        stage("connect to ec2") {
            
            echo 'connecting to ec2'
            sh '''
                ssh -i $SSH_KEY -o StrictHostKeyChecking=no ec2-user@3.27.119.74 << EOF
                cd /home/ec2-user/docker-compose/
                docker-compose down
                docker-compose pull
                docker-compose up -d
                EOF
            '''
        }
        /*
        stage("build backend") {
            
            steps {
                echo "building the backend..."
                sh 'docker build -t $DOCKER_BACKEND_IMG -f backend/Dockerfile .'
            }
        }

        stage("build frontend") {
            
            steps {
                echo "building the frontend..."
                sh 'docker build -t $DOCKER_FRONTEND_IMG -f frontend/Dockerfile .'
            }
        }


        stage("push docker images") {
            steps {
                echo 'pushing docker images...'
                sh 'echo $DOCKER_PAT | docker login -u tankaus --password-stdin'
                sh 'docker push $DOCKER_BACKEND_IMG'
                sh 'docker push $DOCKER_FRONTEND_IMG'
            }
        }

        stage("test") {
            
            steps {
                echo "testing the application... (no tests yet)"
            }
        }
        */
        /*
        stage("deploy") {
            
            steps {
                echo "deploying the app to EC2..."
                sh '''
                ssh -i $SSH_KEY -o StrictHostKeyChecking=no ec2-user@3.27.119.74 << EOF
                cd /home/ec2-user/docker-compose/
                docker-compose down
                docker-compose pull
                docker-compose up -d
                EOF
                '''
            }
        }
        */

    }



}