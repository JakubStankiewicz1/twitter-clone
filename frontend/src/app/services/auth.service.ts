import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

export interface LoginRequest {
  emailOrUsername: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  displayName?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  displayName: string;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  displayName: string;
  bio?: string;
  followersCount: number;
  followingCount: number;
  postsCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';
  private tokenKey = 'auth-token';
  private userKey = 'auth-user';

  private currentUserSubject = new BehaviorSubject<UserResponse | null>(this.getCurrentUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) { }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        map(response => {
          if (response && response.token) {
            this.saveToken(response.token);
            this.saveUser(response);
            this.currentUserSubject.next({
              id: response.id,
              username: response.username,
              email: response.email,
              displayName: response.displayName,
              followersCount: 0,
              followingCount: 0,
              postsCount: 0
            });
          }
          return response;
        })
      );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData)
      .pipe(
        map(response => {
          if (response && response.token) {
            this.saveToken(response.token);
            this.saveUser(response);
            this.currentUserSubject.next({
              id: response.id,
              username: response.username,
              email: response.email,
              displayName: response.displayName,
              followersCount: 0,
              followingCount: 0,
              postsCount: 0
            });
          }
          return response;
        })
      );
  }

  getCurrentUser(): Observable<UserResponse> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.getToken()}`
      })
    };

    return this.http.get<UserResponse>(`${this.apiUrl}/me`, httpOptions)
      .pipe(
        map(user => {
          this.currentUserSubject.next(user);
          return user;
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  private saveUser(user: AuthResponse): void {
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }

  private getCurrentUserFromStorage(): UserResponse | null {
    const userStr = localStorage.getItem(this.userKey);
    if (userStr) {
      const user = JSON.parse(userStr);
      return {
        id: user.id,
        username: user.username,
        email: user.email,
        displayName: user.displayName,
        followersCount: 0,
        followingCount: 0,
        postsCount: 0
      };
    }
    return null;
  }
}
