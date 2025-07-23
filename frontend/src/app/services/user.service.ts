import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse } from './auth.service';

export interface UserSearchResult {
  id: number;
  username: string;
  bio?: string;
  avatar?: string;
  followersCount?: number;
  followingCount?: number;
  createdAt?: string;
}

export interface UserComment {
  id: number;
  content: string;
  username: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = '/api/users';
  private followUrl = '/api/follow';
  private commentsUrl = '/api/comments/user';

  constructor(private http: HttpClient) {}

  searchUsers(query: string): Observable<UserSearchResult[]> {
    return this.http.get<UserSearchResult[]>(`${this.apiUrl}/search?query=${encodeURIComponent(query)}`);
  }

  follow(username: string): Observable<any> {
    return this.http.post<any>(`${this.followUrl}/${encodeURIComponent(username)}`, {}, {
      headers: this.authHeader()
    });
  }

  unfollow(username: string): Observable<any> {
    return this.http.delete<any>(`${this.followUrl}/${encodeURIComponent(username)}`, {
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

  getUserByUsername(username: string): Observable<UserSearchResult> {
    return this.http.get<UserSearchResult>(`${this.apiUrl}/${username}`);
  }

  getUserComments(username: string): Observable<UserComment[]> {
    return this.http.get<UserComment[]>(`${this.commentsUrl}/${username}`);
  }

  updateProfile(data: Partial<UserResponse & { password?: string }>): Observable<UserResponse> {
    return this.http.put<UserResponse>(`/api/users/profile`, data, {
      headers: this.authHeader()
    });
  }

  getOwnProfile(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`/api/users/profile`, {
      headers: this.authHeader()
    });
  }

  private authHeader(): { [header: string]: string } {
    const token = localStorage.getItem('auth-token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }
} 