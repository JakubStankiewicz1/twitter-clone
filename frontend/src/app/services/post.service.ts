import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Post {
  id: number;
  content: string;
  imageUrl?: string;
  createdAt?: string;
  user: { id: number; username: string };
}

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiUrl = '/api/posts';

  constructor(private http: HttpClient) {}

  getFeed(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/feed`);
  }

  addPost(post: { content: string; imageUrl?: string }): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, post, {
      headers: this.authHeader()
    });
  }

  getUserPosts(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/user/${userId}`);
  }

  likePost(postId: number): Observable<{ likes: number }> {
    return this.http.post<{ likes: number }>(`${this.apiUrl}/${postId}/like`, {}, {
      headers: this.authHeader()
    });
  }

  unlikePost(postId: number): Observable<{ likes: number }> {
    return this.http.post<{ likes: number }>(`${this.apiUrl}/${postId}/unlike`, {}, {
      headers: this.authHeader()
    });
  }

  editPost(postId: number, data: { content?: string; imageUrl?: string }): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${postId}`, data, {
      headers: this.authHeader()
    });
  }

  deletePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${postId}`, {
      headers: this.authHeader()
    });
  }

  searchPosts(query: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/search?query=${encodeURIComponent(query)}`);
  }

  getComments(postId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${postId}/comments`);
  }

  addComment(postId: number, content: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${postId}/comments`, { content }, {
      headers: this.authHeader()
    });
  }

  editComment(postId: number, commentId: number, content: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${postId}/comments/${commentId}`, { content }, {
      headers: this.authHeader()
    });
  }

  deleteComment(postId: number, commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${postId}/comments/${commentId}`, {
      headers: this.authHeader()
    });
  }

  getPostsByUsername(username: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/user/${username}`);
  }

  getPostsByUserId(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/user/${userId}`);
  }

  getPostById(postId: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${postId}`);
  }

  private authHeader(): { [header: string]: string } {
    const token = localStorage.getItem('auth-token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }
}
