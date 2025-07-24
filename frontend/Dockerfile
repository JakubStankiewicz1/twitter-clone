# Frontend Dockerfile
FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm ci && npm run build -- --output-path=dist

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80 