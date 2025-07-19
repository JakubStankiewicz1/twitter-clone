import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Message {
  id: number;
  sender: any;
  recipient: any;
  content: string;
  createdAt: string;
  read: boolean;
}

@Injectable({ providedIn: 'root' })
export class MessageService {
  private apiUrl = 'http://localhost:8081/api/messages';

  constructor(private http: HttpClient) {}

  getConversation(user1: number, user2: number): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/conversation?user1=${user1}&user2=${user2}`);
  }

  getAllUserMessages(userId: number): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/user/${userId}`);
  }

  sendMessage(senderId: number, recipientId: number, content: string): Observable<Message> {
    return this.http.post<Message>(this.apiUrl, { senderId, recipientId, content });
  }
} 