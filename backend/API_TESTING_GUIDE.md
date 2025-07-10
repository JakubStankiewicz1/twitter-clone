# 🚀 **X-Clone Backend API - Kompletna Instrukcja Testowania**

## 📋 **Spis Treści**
1. [Uruchomienie Aplikacji](#1-uruchomienie-aplikacji)
2. [Dokumentacja API (Swagger)](#2-dokumentacja-api-swagger)
3. [Testowanie za pomocą Postman](#3-testowanie-za-pomocą-postman)
4. [Testowanie za pomocą cURL](#4-testowanie-za-pomocą-curl)
5. [Endpointy API](#5-endpointy-api)
6. [Przykłady Żądań i Odpowiedzi](#6-przykłady-żądań-i-odpowiedzi)
7. [Uwagi i Tips](#7-uwagi-i-tips)

---

## 🏁 **1. Uruchomienie Aplikacji**

### **Krok 1: Budowanie projektu**
```bash
# W katalogu backend
./mvnw clean install
```

### **Krok 2: Uruchomienie aplikacji**
```bash
# Opcja 1: Maven
./mvnw spring-boot:run

# Opcja 2: Java (po zbudowaniu)
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### **Krok 3: Sprawdzenie czy działa**
- Aplikacja uruchamia się na: **http://localhost:8080**
- Health check: **http://localhost:8080/actuator/health**

---

## 📚 **2. Dokumentacja API (Swagger)**

### **Dostęp do Swagger UI:**
```
http://localhost:8080/swagger-ui/index.html
```

### **JSON Schema API:**
```
http://localhost:8080/v3/api-docs
```

**Swagger zawiera:**
- 📝 Wszystkie dostępne endpointy
- 📊 Modele danych (schemas)
- 🧪 Możliwość testowania "na żywo"
- 📖 Dokumentację parametrów

---

## 🧪 **3. Testowanie za pomocą Postman**

### **Import kolekcji**
1. Otwórz Postman
2. Kliknij **Import**
3. Wklej URL: `http://localhost:8080/v3/api-docs`
4. Postman automatycznie utworzy kolekcję

### **Ustawienie Environment Variables**
```
BASE_URL = http://localhost:8080
JWT_TOKEN = [token po zalogowaniu]
```

---

## 💻 **4. Testowanie za pomocą cURL**

### **Podstawowe komendy cURL:**

#### **GET Request:**
```bash
curl -X GET "http://localhost:8080/api/users" \
  -H "Accept: application/json"
```

#### **POST Request z JSON:**
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "SecurePass123!"
  }'
```

#### **Request z Authorization:**
```bash
curl -X GET "http://localhost:8080/api/posts" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Accept: application/json"
```

---

## 🛣️ **5. Endpointy API**

### **🔐 Autentyfikacja (`/api/auth`)**
| Metoda | Endpoint | Opis | Autoryzacja |
|--------|----------|------|-------------|
| `POST` | `/api/auth/register` | Rejestracja użytkownika | ❌ |
| `POST` | `/api/auth/login` | Logowanie | ❌ |
| `POST` | `/api/auth/refresh` | Odświeżenie tokenu | ✅ |
| `POST` | `/api/auth/logout` | Wylogowanie | ✅ |

### **👤 Użytkownicy (`/api/users`)**
| Metoda | Endpoint | Opis | Autoryzacja |
|--------|----------|------|-------------|
| `GET` | `/api/users` | Lista użytkowników | ✅ |
| `GET` | `/api/users/{id}` | Szczegóły użytkownika | ✅ |
| `PUT` | `/api/users/{id}` | Aktualizacja profilu | ✅ |
| `DELETE` | `/api/users/{id}` | Usunięcie konta | ✅ |
| `POST` | `/api/users/{id}/follow` | Obserwowanie | ✅ |
| `DELETE` | `/api/users/{id}/unfollow` | Przestań obserwować | ✅ |

### **📝 Posty/Tweety (`/api/posts`)**
| Metoda | Endpoint | Opis | Autoryzacja |
|--------|----------|------|-------------|
| `GET` | `/api/posts` | Feed główny | ✅ |
| `POST` | `/api/posts` | Nowy post | ✅ |
| `GET` | `/api/posts/{id}` | Szczegóły posta | ✅ |
| `PUT` | `/api/posts/{id}` | Edycja posta | ✅ |
| `DELETE` | `/api/posts/{id}` | Usunięcie posta | ✅ |
| `POST` | `/api/posts/{id}/like` | Polajkowanie | ✅ |
| `DELETE` | `/api/posts/{id}/unlike` | Usuń like | ✅ |
| `POST` | `/api/posts/{id}/retweet` | Retweet | ✅ |

### **💬 Komentarze (`/api/comments`)**
| Metoda | Endpoint | Opis | Autoryzacja |
|--------|----------|------|-------------|
| `GET` | `/api/posts/{postId}/comments` | Komentarze do posta | ✅ |
| `POST` | `/api/posts/{postId}/comments` | Nowy komentarz | ✅ |
| `PUT` | `/api/comments/{id}` | Edycja komentarza | ✅ |
| `DELETE` | `/api/comments/{id}` | Usunięcie komentarza | ✅ |

### **🔔 Powiadomienia (`/api/notifications`)**
| Metoda | Endpoint | Opis | Autoryzacja |
|--------|----------|------|-------------|
| `GET` | `/api/notifications` | Lista powiadomień | ✅ |
| `PUT` | `/api/notifications/{id}/read` | Oznacz jako przeczytane | ✅ |
| `DELETE` | `/api/notifications/{id}` | Usuń powiadomienie | ✅ |

---

## 📋 **6. Przykłady Żądań i Odpowiedzi**

### **🔐 Rejestracja użytkownika**

#### **Request:**
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jakub123",
    "email": "jakub@example.com",
    "password": "SecurePassword123!",
    "firstName": "Jakub",
    "lastName": "Kowalski",
    "dateOfBirth": "1995-03-15"
  }'
```

#### **Response (Success - 201 Created):**
```json
{
  "id": 1,
  "username": "jakub123",
  "email": "jakub@example.com",
  "firstName": "Jakub",
  "lastName": "Kowalski",
  "createdAt": "2025-07-09T14:30:00Z",
  "isVerified": false
}
```

### **🔑 Logowanie**

#### **Request:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jakub123",
    "password": "SecurePassword123!"
  }'
```

#### **Response (Success - 200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "username": "jakub123",
    "email": "jakub@example.com",
    "firstName": "Jakub",
    "lastName": "Kowalski"
  }
}
```

### **📝 Tworzenie nowego posta**

#### **Request:**
```bash
curl -X POST "http://localhost:8080/api/posts" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "To jest mój pierwszy post na X-Clone! 🚀",
    "imageUrl": "https://example.com/image.jpg"
  }'
```

#### **Response (Success - 201 Created):**
```json
{
  "id": 1,
  "content": "To jest mój pierwszy post na X-Clone! 🚀",
  "imageUrl": "https://example.com/image.jpg",
  "author": {
    "id": 1,
    "username": "jakub123",
    "firstName": "Jakub",
    "lastName": "Kowalski"
  },
  "createdAt": "2025-07-09T14:45:00Z",
  "likesCount": 0,
  "commentsCount": 0,
  "retweetsCount": 0,
  "isLiked": false,
  "isRetweeted": false
}
```

### **👥 Pobieranie feedu**

#### **Request:**
```bash
curl -X GET "http://localhost:8080/api/posts?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Accept: application/json"
```

#### **Response (Success - 200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "content": "To jest mój pierwszy post na X-Clone! 🚀",
      "author": {
        "id": 1,
        "username": "jakub123",
        "firstName": "Jakub"
      },
      "createdAt": "2025-07-09T14:45:00Z",
      "likesCount": 5,
      "commentsCount": 2,
      "retweetsCount": 1,
      "isLiked": false,
      "isRetweeted": false
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "size": 10,
  "number": 0
}
```

### **❤️ Polajkowanie posta**

#### **Request:**
```bash
curl -X POST "http://localhost:8080/api/posts/1/like" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### **Response (Success - 200 OK):**
```json
{
  "message": "Post liked successfully",
  "isLiked": true,
  "likesCount": 6
}
```

### **🔍 Wyszukiwanie użytkowników**

#### **Request:**
```bash
curl -X GET "http://localhost:8080/api/users/search?query=jakub&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Accept: application/json"
```

#### **Response (Success - 200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "username": "jakub123",
      "firstName": "Jakub",
      "lastName": "Kowalski",
      "profileImageUrl": "https://example.com/avatar.jpg",
      "followersCount": 150,
      "followingCount": 89,
      "isFollowing": false
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

---

## ⚠️ **7. Uwagi i Tips**

### **🔑 Autoryzacja**
- Większość endpointów wymaga JWT token
- Token dodawaj w header: `Authorization: Bearer YOUR_TOKEN`
- Token wygasa po 1 godzinie - używaj `/refresh` aby go odnowić

### **📄 Paginacja**
```
?page=0&size=10&sort=createdAt,desc
```

### **🔍 Filtrowanie i Sortowanie**
```
# Posty z konkretnego dnia
?createdAfter=2025-07-09T00:00:00Z&createdBefore=2025-07-10T00:00:00Z

# Sortowanie
?sort=createdAt,desc&sort=likesCount,desc
```

### **📊 Kody Odpowiedzi HTTP**
- `200` - Sukces
- `201` - Utworzono zasób
- `400` - Błędne dane wejściowe
- `401` - Brak autoryzacji
- `403` - Brak uprawnień
- `404` - Nie znaleziono
- `409` - Konflikt (np. email już istnieje)
- `500` - Błąd serwera

### **🐛 Debugowanie**
```bash
# Sprawdź logi aplikacji
tail -f logs/application.log

# Health check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics
```

### **🔧 Zmienne środowiskowe dla testów**
```bash
export BASE_URL=http://localhost:8080
export JWT_TOKEN="your_token_here"

# Używanie w cURL
curl -X GET "$BASE_URL/api/posts" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### **📝 Przykładowy plik .env dla testów**
```env
BASE_URL=http://localhost:8080
TEST_USERNAME=testuser
TEST_PASSWORD=TestPass123!
TEST_EMAIL=test@example.com
```

---

## 🎯 **Gotowe skrypty testowe**

### **Pełny flow rejestracji i logowania:**
```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "🔐 Rejestracja użytkownika..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser'$(date +%s)'",
    "email": "test'$(date +%s)'@example.com",
    "password": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User"
  }')

echo "Odpowiedź rejestracji: $REGISTER_RESPONSE"

echo "🔑 Logowanie..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser'$(date +%s)'",
    "password": "SecurePass123!"
  }')

echo "Odpowiedź logowania: $LOGIN_RESPONSE"

# Wyciągnij token
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.accessToken')

echo "🚀 Token: $TOKEN"

echo "📝 Tworzenie posta..."
POST_RESPONSE=$(curl -s -X POST "$BASE_URL/api/posts" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Test post z API! 🎉"
  }')

echo "Odpowiedź tworzenia posta: $POST_RESPONSE"
```

---

**Powodzenia w testowaniu API! 🚀**

*Jeśli masz problemy, sprawdź logi aplikacji lub skontaktuj się z teamem deweloperskim.*
