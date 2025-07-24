import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService, Message } from '../../services/message.service';
import { AuthService, UserResponse } from '../../services/auth.service';
import { NewMessageModal } from '../../components/messages/new-message-modal';
import { Sidebar } from '../../components/sidebar/sidebar';

@Component({
  selector: 'app-messages',
  imports: [CommonModule, FormsModule, NewMessageModal, Sidebar],
  templateUrl: './messages.html',
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