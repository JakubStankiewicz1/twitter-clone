import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserSearchResult {
  id: number;
  username: string;
  bio?: string;
  avatar?: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = '/api/users';
  private followUrl = '/api/follow';

  constructor(private http: HttpClient) {}

  searchUsers(query: string): Observable<UserSearchResult[]> {
    return this.http.get<UserSearchResult[]>(`${this.apiUrl}/search?query=${encodeURIComponent(query)}`);
  }

  follow(username: string): Observable<any> {
    return this.http.post<any>(`${this.followUrl}/${username}`, {}, {
      headers: this.authHeader()
    });
  }

  unfollow(username: string): Observable<any> {
    return this.http.delete<any>(`${this.followUrl}/${username}`, {
      headers: this.authHeader()
    });
  }

  getFollowers(username: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.followUrl}/${username}/followers`);
  }

  getFollowing(username: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.followUrl}/${username}/following`);
  }

  getFollowCounts(username: string): Observable<{followers: number, following: number}> {
    return this.http.get<{followers: number, following: number}>(`${this.followUrl}/${username}/counts`);
  }

  private authHeader(): { [header: string]: string } {
    const token = localStorage.getItem('auth-token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }
} 