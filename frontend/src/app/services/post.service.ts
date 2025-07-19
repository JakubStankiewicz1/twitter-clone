import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Post {
  id: number;
  userId: number;
  content: string;
  mediaUrls?: { url: string; type: string; altText: string }[];
  createdAt?: string;
  displayName?: string;
  username?: string;
}

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiUrl = 'http://localhost:8081/api/posts';

  constructor(private http: HttpClient) {}

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/all`);
  }

  addPost(post: Partial<Post>): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, post);
  }

  getForYouPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/foryou`);
  }

  getFollowingPosts(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/following?userId=${userId}`);
  }
}
