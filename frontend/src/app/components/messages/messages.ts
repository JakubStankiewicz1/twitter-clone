import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService, Message } from '../../services/message.service';
import { AuthService, UserResponse } from '../../services/auth.service';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './messages.html',
  styleUrl: './messages.scss'
})
export class Messages {
  conversations: { user: any, lastMessage: Message }[] = [];
  selectedUser: any = null;
  messages: Message[] = [];
  currentUser: UserResponse | null = null;
  newMessage = '';
  loading = false;

  constructor(private messageService: MessageService, private authService: AuthService) {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) this.loadConversations();
    });
  }

  loadConversations() {
    if (!this.currentUser) return;
    this.loading = true;
    this.messageService.getAllUserMessages(this.currentUser.id).subscribe(msgs => {
      const map: { [userId: number]: { user: any, lastMessage: Message } } = {};
      msgs.forEach(msg => {
        const other = msg.sender.id === this.currentUser!.id ? msg.recipient : msg.sender;
        if (!map[other.id] || new Date(msg.createdAt) > new Date(map[other.id].lastMessage.createdAt)) {
          map[other.id] = { user: other, lastMessage: msg };
        }
      });
      this.conversations = Object.values(map).sort((a, b) => new Date(b.lastMessage.createdAt).getTime() - new Date(a.lastMessage.createdAt).getTime());
      this.loading = false;
    });
  }

  selectConversation(user: any) {
    this.selectedUser = user;
    this.loadMessages();
  }

  loadMessages() {
    if (!this.currentUser || !this.selectedUser) return;
    this.loading = true;
    this.messageService.getConversation(this.currentUser.id, this.selectedUser.id).subscribe(msgs => {
      this.messages = msgs;
      this.loading = false;
    });
  }

  sendMessage() {
    if (!this.currentUser || !this.selectedUser || !this.newMessage.trim()) return;
    this.messageService.sendMessage(this.currentUser.id, this.selectedUser.id, this.newMessage).subscribe(msg => {
      this.newMessage = '';
      this.loadMessages();
      this.loadConversations();
    });
  }
} 