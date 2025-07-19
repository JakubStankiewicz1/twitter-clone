# 🚀 Szybki Start: Testowanie API X-Clone w Postmanie

Ten przewodnik pokazuje krok po kroku, jak testować najważniejsze funkcje Twojego API w Postmanie.

---

## 1. Import kolekcji do Postmana

1. Otwórz Postmana.
2. Kliknij **Import**.
3. Wklej URL: `http://localhost:8081/v3/api-docs` (upewnij się, że backend działa na porcie 8081!).
4. Zatwierdź – Postman utworzy kolekcję endpointów.

---

## 2. Ustaw zmienne środowiskowe

W Postmanie utwórz Environment:
- `BASE_URL = http://localhost:8081`
- `JWT_TOKEN = [tu wkleisz token po zalogowaniu]`
- `USER_ID = [tu wkleisz id użytkownika z odpowiedzi logowania lub rejestracji]`

---

## 3. Przykłady żądań

### 🔐 Rejestracja użytkownika
- **Metoda:** POST
- **URL:** `{{BASE_URL}}/api/auth/register`
- **Body (raw/JSON):**
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "TestPass123!",
  "firstName": "Test",
  "lastName": "User"
}
```
- **Nagłówki:**
  - Content-Type: application/json

---

### 🔑 Logowanie
- **Metoda:** POST
- **URL:** `{{BASE_URL}}/api/auth/login`
- **Body (raw/JSON):**
```json
{
  "username": "testuser",
  "password": "TestPass123!"
}
```
- **Nagłówki:**
  - Content-Type: application/json

Po zalogowaniu skopiuj pole `accessToken` z odpowiedzi i ustaw jako `JWT_TOKEN` w środowisku Postmana. Skopiuj także pole `id` i ustaw jako `USER_ID`.

---

### 📝 Tworzenie nowego posta
- **Metoda:** POST
- **URL:** `{{BASE_URL}}/api/posts`
- **Body (raw/JSON):**
```json
{
  "userId": {{USER_ID}},
  "content": "To jest mój pierwszy post!",
  "mediaUrls": [
    {
      "url": "https://example.com/obrazek.jpg",
      "type": "image",
      "altText": "Opis obrazka"
    }
  ]
}
```
- **Nagłówki:**
  - Content-Type: application/json
  - Authorization: Bearer {{JWT_TOKEN}}

> **Uwaga:** Pole `userId` jest wymagane! Jeśli nie chcesz dodawać obrazka, możesz pominąć pole `mediaUrls`.

---

### 👀 Pobieranie feedu (postów)
- **Metoda:** GET
- **URL:** `{{BASE_URL}}/api/posts?page=0&size=10`
- **Nagłówki:**
  - Authorization: Bearer {{JWT_TOKEN}}

---

### 📋 Pobieranie wszystkich postów (bez paginacji)
- **Metoda:** GET
- **URL:** `{{BASE_URL}}/api/posts/all`
- **Nagłówki:**
  - Authorization: Bearer {{JWT_TOKEN}}

---

### ❤️ Lajkowanie posta
- **Metoda:** POST
- **URL:** `{{BASE_URL}}/api/posts/1/like` (zamień 1 na ID posta)
- **Nagłówki:**
  - Authorization: Bearer {{JWT_TOKEN}}

---

### 👤 Wyszukiwanie użytkowników
- **Metoda:** GET
- **URL:** `{{BASE_URL}}/api/users/search?query=test&page=0&size=10`
- **Nagłówki:**
  - Authorization: Bearer {{JWT_TOKEN}}

---

## 4. Ważne uwagi
- Większość endpointów wymaga nagłówka `Authorization: Bearer {{JWT_TOKEN}}`.
- Zawsze ustawiaj `Content-Type: application/json` przy POST/PUT.
- Jeśli dostaniesz błąd 401 – sprawdź, czy token jest aktualny.
- Jeśli dostaniesz błąd 400 przy tworzeniu posta, upewnij się, że w body jest pole `userId`.

---

**Powodzenia!** Jeśli coś nie działa, sprawdź logi backendu lub napisz do zespołu.
