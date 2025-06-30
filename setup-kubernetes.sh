# rebuild images and load into minikube (using git bash MINGW64)

echo "### restart minikube ###"
#minikube delete
minikube start --driver=docker

echo "### enabling ingress addon ###"
# minikube addons enable ingress

echo "### deleting existing deployments and services ###"
kubectl delete ingress quiz-ingress --ignore-not-found
kubectl delete service backend-service-0 backend-service-1 backend-service-2 --ignore-not-found
kubectl delete service frontend-service redis-service gateway-service --ignore-not-found
kubectl delete deployment backend-deployment frontend-deployment redis gateway-deployment --ignore-not-found
kubectl delete statefulset backend-deployment --ignore-not-found

echo "### building images ###"
docker build -t tankaus/frontend:latest ./frontend
docker build -t tankaus/backend:latest ./backend


echo "### loading images into minikube ###"
minikube image load tankaus/frontend:latest
minikube image load tankaus/backend:latest

kubectl apply -f redis.yaml
kubectl wait --for=condition=ready pod --all --timeout=60s
kubectl apply -f frontend.yaml
kubectl apply -f backend.yaml
kubectl apply -f ingress.yaml

echo "### waiting for pods to be ready ###"
kubectl wait --for=condition=ready pod --all --timeout=60s

echo "### listing pods and services ###"
kubectl get pods -o wide
kubectl get services
kubectl get ingress

echo "### restarting backend deployments ###"
kubectl rollout restart statefulset backend-deployment
kubectl wait --for=condition=ready pod --all --timeout=60s

echo "### minikube ip, vs /etc/hosts ###"
echo "minikube: <$(minikube ip)>"