# Twitter Clone

Projekt Twitter/X Clone – aplikacja społecznościowa z funkcjami:
- Rejestracja i logowanie użytkownika
- Tworzenie, edycja, usuwanie postów
- Komentarze, lajki, obserwowanie
- Prywatne wiadomości (chat)
- Wyszukiwanie użytkowników i postów

---

## Wymagania
- **Node.js** (v18+)
- **npm** (v9+)
- **Java** (JDK 21)
- **Maven** (nie wymagany, jest wrapper `mvnw`)
- **PostgreSQL** (v14+)

---

## Backend (Spring Boot)

1. **Skonfiguruj bazę danych PostgreSQL:**
   - Utwórz bazę: `backend_db`
   - Domyślne dane logowania (możesz zmienić w `backend/src/main/resources/application.properties`):
     - user: `postgres`
     - password: `root`

2. **Uruchom backend:**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   # lub na Windows:
   mvnw.cmd spring-boot:run
   ```
   Backend domyślnie działa na porcie **8081**: http://localhost:8081

---

## Frontend (Angular)

1. **Zainstaluj zależności:**
   ```bash
   cd frontend
   npm ci
   ```
2. **Uruchom frontend:**
   ```bash
   npm start
   # lub
   npm run start
   ```
   Frontend domyślnie działa na porcie **4200**: http://localhost:4200

---

**Autor:** Jakub Stankiewicz 
