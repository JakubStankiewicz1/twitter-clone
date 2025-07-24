# Hosting Twitter-clone na Render.com (backend + frontend + PostgreSQL)

## 1. Wymagane pliki
- `Dockerfile` dla backendu (Spring Boot)
- `Dockerfile` dla frontend (Angular)
- (opcjonalnie) `docker-compose.yml` do lokalnych testów
- Konfiguracja bazy PostgreSQL na Render

---

## 2. Backend (Spring Boot) – Dockerfile

W katalogu `backend/` utwórz plik `Dockerfile`:

```Dockerfile
# Backend Dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 3. Frontend (Angular) – Dockerfile

W katalogu `frontend/` utwórz plik `Dockerfile`:

```Dockerfile
# Frontend Dockerfile
FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm ci && npm run build -- --output-path=dist

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

Utwórz plik `nginx.conf` w katalogu `frontend/`:
```nginx
server {
  listen 80;
  server_name _;
  root /usr/share/nginx/html;
  index index.html;
  location / {
    try_files $uri $uri/ /index.html;
  }
  location /api/ {
    proxy_pass http://backend:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }
}
```

---

## 4. (Opcjonalnie) docker-compose do lokalnych testów

W katalogu głównym utwórz plik `docker-compose.yml`:

```yaml
version: '3.8'
services:
  db:
    image: postgres:15
    restart: always
    environment:
      POSTGRES_DB: backend_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  backend:
    build: ./backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/backend_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      - db
    ports:
      - "8080:8080"

  frontend:
    build: ./frontend
    depends_on:
      - backend
    ports:
      - "80:80"

volumes:
  pgdata:
```

---

## 5. Deploy na Render.com

### Backend:
1. Stwórz nowy Web Service na Render, wskaż repozytorium i katalog `backend/`.
2. Wybierz Docker jako środowisko build.
3. Ustaw port: `8080`.
4. Ustaw zmienne środowiskowe:
   - `SPRING_DATASOURCE_URL` (np. z Render PostgreSQL)
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
5. Połącz z bazą PostgreSQL (stwórz ją w Render, skopiuj dane do zmiennych).

### Frontend:
1. Stwórz nowy Web Service na Render, wskaż repozytorium i katalog `frontend/`.
2. Wybierz Docker jako środowisko build.
3. Ustaw port: `80`.
4. (Opcjonalnie) ustaw zmienną środowiskową `API_URL` jeśli korzystasz z dynamicznego endpointu.

### Baza danych:
- W Render utwórz PostgreSQL Database, skopiuj dane do backendu.

---

## 6. Ważne uwagi
- Upewnij się, że backend i frontend mają poprawne adresy API (np. `/api/` proxy w nginx.conf frontend).
- W Render backend i frontend będą miały publiczne URL – ustaw CORS jeśli trzeba.
- Jeśli korzystasz z proxy w dev, na produkcji frontend musi kierować zapytania do backendu po publicznym URL.

---

## 7. Testowanie lokalne

```bash
docker-compose up --build
```
- Frontend: http://localhost/
- Backend: http://localhost:8080/
- Baza: localhost:5432 (postgres/postgres)

---

**Gotowe!** 