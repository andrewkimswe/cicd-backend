# 빌드 단계
FROM node:alpine as build
WORKDIR /app
COPY src/main/frontend/package.json src/main/frontend/package-lock.json ./
RUN npm install
COPY src/main/frontend/ ./
RUN npm run build

# 실행 단계
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
