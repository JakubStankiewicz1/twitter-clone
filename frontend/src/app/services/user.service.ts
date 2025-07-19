import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserSearchResult {
  id: number;
  username: string;
  displayName: string;
  email: string;
  bio?: string;
  followersCount: number;
  followingCount: number;
  postsCount: number;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = 'http://localhost:8081/api/users';
  private followUrl = 'http://localhost:8081/api/follow';

  constructor(private http: HttpClient) {}

  searchUsers(query: string): Observable<UserSearchResult[]> {
    return this.http.get<UserSearchResult[]>(`${this.apiUrl}/search?q=${encodeURIComponent(query)}`);
  }

  toggleFollow(followerId: number, followingId: number): Observable<any> {
    return this.http.post<any>(`${this.followUrl}/toggle`, { followerId, followingId });
  }

  checkIfFollowing(followerId: number, followingId: number): Observable<{isFollowing: boolean}> {
    return this.http.get<{isFollowing: boolean}>(`${this.followUrl}/check?followerId=${followerId}&followingId=${followingId}`);
  }
} 