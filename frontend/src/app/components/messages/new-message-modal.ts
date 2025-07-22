import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, UserSearchResult } from '../../services/user.service';

@Component({
  selector: 'app-new-message-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-backdrop" (click)="closeModal()"></div>
    <div class="post-modal-container" (click)="$event.stopPropagation()">
      <button class="close-btn" type="button" (click)="closeModal()" aria-label="Close">×</button>
      <div class="post-modal-content" style="flex-direction: column; min-width: 350px; max-width: 400px;">
        <h2 style="font-size: 1.3rem; font-weight: bold; margin-bottom: 1rem;">New message</h2>
        <input type="text" [(ngModel)]="searchQuery" (ngModelChange)="onSearchChange()" placeholder="Search people..." class="search-input" autofocus style="margin-bottom: 1rem; width: 100%; padding: 0.7rem; border-radius: 999px; border: none; background: #222; color: #fff; font-size: 1rem;" />
        <div *ngIf="loading" style="color: #1da1f2; text-align: center;">Loading...</div>
        <div *ngIf="!loading && searchResults.length === 0 && searchQuery.trim()" style="color: #888; text-align: center;">No users found.</div>
        <div *ngFor="let user of searchResults" class="user-result" (click)="selectUser(user)">
          <div class="avatar" style="width:36px; height:36px; font-size:1rem;">{{ user.username.charAt(0).toUpperCase() }}</div>
          <div style="margin-left: 12px;">
            <div style="font-weight: 600; color: #fff;">{{ user.username }}</div>
            <div style="color: #aaa; font-size: 0.95rem;">{{ user.bio }}</div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrl: '../sidebar/post-modal.scss'
})
export class NewMessageModal {
  @Output() close = new EventEmitter<void>();
  @Output() userSelected = new EventEmitter<UserSearchResult>();
  searchQuery = '';
  searchResults: UserSearchResult[] = [];
  loading = false;

  constructor(private userService: UserService) {}

  closeModal() {
    this.close.emit();
  }

  onSearchChange() {
    const query = this.searchQuery.trim();
    if (!query) {
      this.searchResults = [];
      return;
    }
    this.loading = true;
    this.userService.searchUsers(query).subscribe({
      next: (results) => {
        this.searchResults = results;
        this.loading = false;
      },
      error: () => {
        this.searchResults = [];
        this.loading = false;
      }
    });
  }

  selectUser(user: UserSearchResult) {
    this.userSelected.emit(user);
    this.closeModal();
  }
} 