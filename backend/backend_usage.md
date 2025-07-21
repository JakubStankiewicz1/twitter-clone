# Instrukcja testowania endpointów użytkownika

Poniżej znajdziesz instrukcje testowania każdego z endpointów użytkownika (rejestracja, logowanie, profil, edycja, usuwanie konta). Każdy krok będzie aktualizowany wraz z rozwojem backendu.

## 1. Rejestracja użytkownika

- Endpoint: `POST /api/users/register`
- Body (JSON):
  ```json
  {
    "username": "twoj_login",
    "email": "twoj_email",
    "password": "twoje_haslo"
  }
  ```
- Oczekiwany rezultat: 201 Created, zwraca dane użytkownika (bez hasła).

## 2. Logowanie użytkownika

- Endpoint: `POST /api/users/login`
- Body (JSON):
  ```json
  {
    "username": "twoj_login",
    "password": "twoje_haslo"
  }
  ```
- Oczekiwany rezultat: 200 OK, zwraca JWT token.

## 3. Weryfikacja tokenu

- Endpoint: `GET /api/users/verify`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK, zwraca dane użytkownika.

## 4. Pobieranie profilu użytkownika

- Endpoint: `GET /api/users/profile`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK, zwraca dane profilu.

## 5. Edycja profilu

- Endpoint: `PUT /api/users/profile`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Body (JSON):
  ```json
  {
    "bio": "nowe_bio",
    "avatar": "url_do_avatara",
    "password": "nowe_haslo"
  }
  ```
- Oczekiwany rezultat: 200 OK, zwraca zaktualizowane dane profilu.

## 6. Usuwanie konta

- Endpoint: `DELETE /api/users/profile`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 204 No Content.

---

## Jak testować

1. Użyj narzędzia typu Postman, Insomnia lub curl.
2. Wysyłaj żądania zgodnie z powyższymi instrukcjami.
3. Sprawdzaj odpowiedzi i statusy HTTP.
4. W przypadku błędów sprawdź komunikaty zwrotne.

Każdy nowy endpoint będzie tu opisywany.

---

## Endpointy postów

### 1. Tworzenie postu
- Endpoint: `POST /api/posts`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Body (JSON):
  ```json
  {
    "content": "treść posta",
    "imageUrl": "url_do_obrazka"
  }
  ```
- Oczekiwany rezultat: 201 Created, zwraca dane postu.

### 2. Edycja postu
- Endpoint: `PUT /api/posts/{id}`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Body (JSON):
  ```json
  {
    "content": "nowa_treść",
    "imageUrl": "nowy_url"
  }
  ```
- Oczekiwany rezultat: 200 OK, zwraca zaktualizowany post.

### 3. Usuwanie postu
- Endpoint: `DELETE /api/posts/{id}`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 204 No Content.

### 4. Feed główny
- Endpoint: `GET /api/posts/feed`
- Oczekiwany rezultat: 200 OK, lista wszystkich postów.

### 5. Pobieranie postów użytkownika
- Endpoint: `GET /api/posts/user/{userId}`
- Oczekiwany rezultat: 200 OK, lista postów danego użytkownika.

### 6. Lajkowanie postu
- Endpoint: `POST /api/posts/{id}/like`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK, zwraca aktualny licznik lajków.

### 7. Odlajkowanie postu
- Endpoint: `POST /api/posts/{id}/unlike`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK, zwraca aktualny licznik lajków.

---

## Endpointy komentarzy

### 1. Dodawanie komentarza
- Endpoint: `POST /api/posts/{postId}/comments`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Body (JSON):
  ```json
  {
    "content": "treść komentarza"
  }
  ```
- Oczekiwany rezultat: 201 Created, zwraca dane komentarza.

### 2. Pobieranie komentarzy pod postem
- Endpoint: `GET /api/posts/{postId}/comments`
- Oczekiwany rezultat: 200 OK, lista komentarzy.

### 3. Usuwanie komentarza
- Endpoint: `DELETE /api/posts/{postId}/comments/{commentId}`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 204 No Content.

### 4. Edycja komentarza
- Endpoint: `PUT /api/posts/{postId}/comments/{commentId}`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Body (JSON):
  ```json
  {
    "content": "nowa treść komentarza"
  }
  ```
- Oczekiwany rezultat: 200 OK, zwraca zaktualizowany komentarz.

---

## Endpointy wiadomości prywatnych

### 1. Tworzenie rozmowy
- Endpoint: `POST /api/conversations/with/{otherUsername}`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK, zwraca dane rozmowy.

### 2. Lista rozmów użytkownika
- Endpoint: `GET /api/conversations`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK, lista rozmów.

### 3. Wysyłanie wiadomości
- Endpoint: `POST /api/conversations/{conversationId}/messages`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Body (JSON):
  ```json
  {
    "content": "treść wiadomości"
  }
  ```
- Oczekiwany rezultat: 201 Created, zwraca dane wiadomości.

### 4. Pobieranie wiadomości z rozmowy
- Endpoint: `GET /api/conversations/{conversationId}/messages`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK, lista wiadomości.

### 5. Oznaczanie wiadomości jako przeczytanej
- Endpoint: `POST /api/conversations/{conversationId}/messages/{messageId}/read`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK.

### 6. Usuwanie wiadomości
- Endpoint: `DELETE /api/conversations/{conversationId}/messages/{messageId}`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 204 No Content.

---

## Endpointy obserwowania (follow)

### 1. Obserwuj użytkownika
- Endpoint: `POST /api/follow/{username}`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK, potwierdzenie obserwowania.

### 2. Przestań obserwować użytkownika
- Endpoint: `DELETE /api/follow/{username}`
- Header: `Authorization: Bearer <JWT_TOKEN>`
- Oczekiwany rezultat: 200 OK, potwierdzenie odlajkowania.

### 3. Lista followers
- Endpoint: `GET /api/follow/{username}/followers`
- Oczekiwany rezultat: 200 OK, lista loginów obserwujących.

### 4. Lista following
- Endpoint: `GET /api/follow/{username}/following`
- Oczekiwany rezultat: 200 OK, lista loginów obserwowanych.

### 5. Liczniki followers/following
- Endpoint: `GET /api/follow/{username}/counts`
- Oczekiwany rezultat: 200 OK, JSON: `{ "followers": liczba, "following": liczba }`

---

## Endpoint wyszukiwania użytkowników

### 1. Wyszukiwanie użytkowników po loginie/nazwie
- Endpoint: `GET /api/users/search?query=szukany_login`
- Oczekiwany rezultat: 200 OK, lista użytkowników (id, username, avatar, bio) pasujących do zapytania (częściowe dopasowanie, bez rozróżniania wielkości liter).
- Przykład odpowiedzi:
  ```json
  [
    {
      "id": 1,
      "username": "janek",
      "avatar": "url_do_avatara",
      "bio": "opis"
    },
    ...
  ]
  ```

---

## Endpoint wyszukiwania postów

### 1. Wyszukiwanie postów po słowach kluczowych
- Endpoint: `GET /api/posts/search?query=szukane_słowo`
- Oczekiwany rezultat: 200 OK, lista postów, których treść zawiera podane słowo (ignorując wielkość liter).
- Przykład odpowiedzi:
  ```json
  [
    {
      "id": 1,
      "content": "To jest post o kotach",
      "imageUrl": "url_do_obrazka",
      "createdAt": "2024-07-01T12:00:00",
      "user": { "id": 2, "username": "janek" }
    },
    ...
  ]
  ```

---
