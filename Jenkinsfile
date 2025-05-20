pipeline {

    agent any

    environment {
        DOCKER_PAT = credentials('docker-pat')
        SSH_KEY = credentials('ec2-ssh-key')
        DOCKER_FRONTEND_IMG = 'tankaus/frontend'
        DOCKER_BACKEND_IMG =  'tankaus/backend'
        EC2_HOST = 'ec2-user@54.79.146.28'
        EC2_PATH = '/home/ec2-user/'
        LOCAL_BACKEND = 'backend/'
        LOCAL_FRONTEND = 'frontend/'
    }

    stages {
        // in the future, should compress files into .tar.gz, but for now transfer everything
        stage("SCP source code to EC2") {
            steps {
            /*
            echo 'copying backend to EC2...'
            sh '''
                scp -r -i $SSH_KEY -o StrictHostKeyChecking=no $LOCAL_BACKEND $EC2_HOST:$EC2_PATH
            '''
            echo 'copying frontend to EC2...'
            sh '''
                scp -r -i $SSH_KEY -o StrictHostKeyChecking=no $LOCAL_FRONTEND $EC2_HOST:$EC2_PATH
            '''
            */
            echo 'copying docker-compose.yaml to EC2...'
            sh '''
                scp -r -i $SSH_KEY -o StrictHostKeyChecking=no docker-compose.yaml $EC2_HOST:$EC2_PATH
            '''
            sh '''
                ssh -i $SSH_KEY -o StrictHostKeyChecking=no $EC2_HOST "
                cat docker-compose.yaml
                "
            '''
            }
        }
        /*
        stage("Build Docker Images on EC2") {
            steps {
                echo 'building backend on EC2...'
                sh '''
                    ssh -i $SSH_KEY -o StrictHostKeyChecking=no $EC2_HOST "
                    cd backend &&
                    docker build -t $DOCKER_BACKEND_IMG:latest .
                    "
                '''
                echo 'building frontend on EC2...'
                sh '''
                    ssh -i $SSH_KEY -o StrictHostKeyChecking=no $EC2_HOST "
                    cd frontend &&
                    docker build -t $DOCKER_FRONTEND_IMG:latest .
                    "
                '''
            }
        }
        */
        stage("Test") {
            steps {
                // test whether docker compose is up for now
                echo 'testing...'
                sh '''
                    ssh -i $SSH_KEY -o StrictHostKeyChecking=no $EC2_HOST "
                    docker-compose ps
                    "
                '''
            }
        }
        /*
        stage("Push Docker Images") {
            steps {
                echo 'pushing recently built docker images...'
                sh '''
                ssh -i $SSH_KEY -o StrictHostKeyChecking=no $EC2_HOST "
                echo $DOCKER_PAT | docker login -u tankaus --password-stdin &&
                docker push $DOCKER_BACKEND_IMG &&
                docker push $DOCKER_FRONTEND_IMG"
                '''
            }
        }
        */
        stage("Deploy") {
            steps {
                echo "deploying the app on EC2..."
                sh '''
                ssh -i $SSH_KEY -o StrictHostKeyChecking=no $EC2_HOST "
                cd /home/ec2-user &&
                docker-compose down &&
                docker-compose pull &&
                docker-compose up -d &&
                "
                '''
            }
        }    
    }

}