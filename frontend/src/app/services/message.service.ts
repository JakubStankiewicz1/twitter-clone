import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Message {
  id: number;
  sender: string; // username nadawcy
  receiver: string; // username odbiorcy
  content: string;
  createdAt: string;
  read: boolean;
}

@Injectable({ providedIn: 'root' })
export class MessageService {
  private apiUrl = '/api/conversations';

  constructor(private http: HttpClient) {}

  createConversation(otherUsername: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/with/${otherUsername}`, {}, {
      headers: this.authHeader()
    });
  }

  getConversations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`, {
      headers: this.authHeader()
    });
  }

  getMessages(conversationId: number): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/${conversationId}/messages`, {
      headers: this.authHeader()
    });
  }

  sendMessage(conversationId: number, content: string): Observable<Message> {
    return this.http.post<Message>(`${this.apiUrl}/${conversationId}/messages`, { content }, {
      headers: this.authHeader()
    });
  }

  markAsRead(conversationId: number, messageId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${conversationId}/messages/${messageId}/read`, {}, {
      headers: this.authHeader()
    });
  }

  deleteMessage(conversationId: number, messageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${conversationId}/messages/${messageId}`, {
      headers: this.authHeader()
    });
  }

  private authHeader(): { [header: string]: string } {
    const token = localStorage.getItem('auth-token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }
} 