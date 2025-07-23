import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService, Message } from '../../services/message.service';
import { AuthService, UserResponse } from '../../services/auth.service';
import { NewMessageModal } from '../../components/messages/new-message-modal';
import { AppLayout } from '../../components/app-layout/app-layout';

@Component({
  selector: 'app-messages',
  imports: [CommonModule, FormsModule, NewMessageModal, AppLayout],
  template: `
    <app-layout>
      <div class="messages-3col-container">
        <!-- Środkowa kolumna: powitanie -->
        <div class="messages-welcome">
          <div class="welcome-title">Welcome to your inbox!</div>
          <div class="welcome-desc">Drop a line, share posts and more with private conversations between you and others on X.</div>
          <button class="primary-btn" (click)="openNewMessageModal()">Write a message</button>
        </div>
        <!-- Prawa kolumna: select a message -->
        <div class="messages-select" *ngIf="!selectedUser">
          <div class="select-title">Select a message</div>
          <div class="select-desc">Choose from your existing conversations, start a new one, or just keep swimming.</div>
          <button class="primary-btn" (click)="openNewMessageModal()">New message</button>
          <div *ngIf="loading" class="loader">Loading conversations...</div>
          <div *ngIf="!loading && conversations.length === 0" class="empty">No conversations yet.</div>
          <div *ngIf="!loading && conversations.length > 0">
            <div class="conversations-list">
              <div *ngFor="let conv of conversations" class="conversation" (click)="selectConversation(conv)">
                <div class="avatar">{{ conv.otherUser.username.charAt(0).toUpperCase() }}</div>
                <div class="conv-info">
                  <div class="conv-name">{{ conv.otherUser.username }}</div>
                  <div class="conv-last">{{ conv.lastMessage ? conv.lastMessage.content : 'No messages yet' }}</div>
                </div>
                <div class="conv-date">
                  {{ conv.lastMessage && conv.lastMessage.createdAt ? (conv.lastMessage.createdAt | date:'short') : '' }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- Widok czatu -->
        <div class="chat-window" *ngIf="selectedUser">
          <div class="chat-header">
            <button class="back-btn" (click)="backToConversations()" title="Back to conversations">←</button>
            <div class="avatar">{{ selectedUser.username.charAt(0).toUpperCase() }}</div>
            <div class="chat-user-info">
              <div class="chat-name">{{ selectedUser.username }}</div>
            </div>
          </div>
          <div class="chat-messages">
            <div *ngIf="loading" class="loader">Loading messages...</div>
            <div *ngIf="!loading && messages.length === 0" class="empty">No messages yet. Say hello!</div>
            <div *ngFor="let msg of messages" class="chat-message" [class.me]="msg.sender.id === currentUser?.id">
              <div class="msg-content">{{ msg.content }}</div>
              <div class="msg-date">{{ msg.createdAt | date:'shortTime' }}</div>
            </div>
          </div>
          <form class="chat-input" (ngSubmit)="sendMessage()">
            <input [(ngModel)]="newMessage" name="newMessage" placeholder="Type a message..." autocomplete="off" />
            <button type="submit" [disabled]="!newMessage.trim()">Send</button>
          </form>
        </div>
        <app-new-message-modal *ngIf="showNewMessageModal" (close)="closeNewMessageModal()" (userSelected)="onUserSelected($event)"></app-new-message-modal>
      </div>
    </app-layout>
  `,
  styleUrl: './messages.scss'
})
export class Messages implements OnDestroy {
  conversations: { otherUser: any, id: any, lastMessage: any }[] = [];
  selectedUser: any = null;
  messages: Message[] = [];
  currentUser: UserResponse | null = null;
  newMessage = '';
  loading = false;
  showNewMessageModal = false;
  private pollingInterval: any = null;

  constructor(private messageService: MessageService, private authService: AuthService) {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) this.loadConversations();
    });
  }

  ngOnDestroy() {
    this.clearPolling();
  }

  loadConversations() {
    if (!this.currentUser) return;
    this.loading = true;
    this.messageService.getConversations().subscribe((convs: any[]) => {
      // Oczekujemy: conv.id, conv.otherUser, conv.lastMessage
      this.conversations = convs.map(conv => ({
        otherUser: conv.otherUser,
        id: conv.id,
        lastMessage: conv.lastMessage ?? null
      }));
      this.loading = false;
    });
  }

  selectConversation(conv: any) {
    this.selectedUser = {
      ...conv.otherUser,
      conversationId: conv.id
    };
    this.loadMessages();
    this.startPolling();
  }

  startPolling() {
    this.clearPolling();
    this.pollingInterval = setInterval(() => {
      this.loadMessages(false);
    }, 2000);
  }

  clearPolling() {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
      this.pollingInterval = null;
    }
  }

  loadMessages(scrollToBottom: boolean = true) {
    if (!this.currentUser || !this.selectedUser) return;
    this.loading = true;
    if (!this.selectedUser || !this.selectedUser.conversationId) return;
    this.messageService.getMessages(this.selectedUser.conversationId).subscribe((msgs: Message[]) => {
      this.messages = msgs;
      this.loading = false;
      if (scrollToBottom) {
        setTimeout(() => {
          const chatMessages = document.querySelector('.chat-messages');
          if (chatMessages) chatMessages.scrollTop = chatMessages.scrollHeight;
        }, 100);
      }
    });
  }

  sendMessage() {
    if (!this.currentUser || !this.selectedUser || !this.newMessage.trim()) return;
    if (!this.selectedUser || !this.selectedUser.conversationId) return;
    this.messageService.sendMessage(this.selectedUser.conversationId, this.newMessage).subscribe((msg: Message) => {
      this.newMessage = '';
      this.loadMessages();
      this.loadConversations();
    });
  }

  openNewMessageModal() {
    this.showNewMessageModal = true;
  }

  closeNewMessageModal() {
    this.showNewMessageModal = false;
  }

  onUserSelected(user: any) {
    // Tworzymy/pobieramy konwersację i przechodzimy do czatu
    this.showNewMessageModal = false;
    this.messageService.createConversation(user.username).subscribe(conv => {
      // Zakładamy, że backend zwraca id konwersacji i usera
      this.selectedUser = {
        ...user,
        conversationId: conv.id
      };
      this.loadMessages();
      this.loadConversations();
    });
  }

  backToConversations() {
    this.selectedUser = null;
    this.clearPolling();
  }
} 