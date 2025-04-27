FROM node:22-alpine AS build
WORKDIR /frontend
COPY package.*json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine AS prod
WORKDIR /frontend
COPY --from=build /frontend/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]