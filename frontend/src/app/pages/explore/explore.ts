import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PostService, Post } from '../../services/post.service';
import { UserService, UserSearchResult } from '../../services/user.service';
import { AppLayout } from '../../components/app-layout/app-layout';

@Component({
  selector: 'app-explore',
  imports: [CommonModule, FormsModule, RouterModule, AppLayout],
  template: `
    <app-layout>
      <div class="explore-container">
        <div class="explore-hero">
          <h1>Odkrywaj posty i użytkowników</h1>
          <form (ngSubmit)="onSearch()" class="explore-search-form">
            <input type="text" [(ngModel)]="searchQuery" name="searchQuery" placeholder="Wyszukaj posty lub użytkowników..." autofocus />
            <button type="submit" [disabled]="!searchQuery.trim() || searching">Szukaj</button>
          </form>
          <div *ngIf="error" class="explore-error">{{ error }}</div>
        </div>
        <div class="explore-results" *ngIf="searchQuery.trim()">
          <div class="explore-section" *ngIf="userResults.length > 0">
            <h2>Użytkownicy</h2>
            <div class="user-list">
              <div *ngFor="let user of userResults" class="user-item" [routerLink]="['/user', user.username]" style="cursor:pointer;">
                <div class="user-avatar">{{ user.username.charAt(0).toUpperCase() }}</div>
                <div class="user-info">
                  <div class="user-name">{{ user.username }}</div>
                  <div class="user-bio" *ngIf="user.bio">{{ user.bio }}</div>
                </div>
              </div>
            </div>
          </div>
          <div class="explore-section" *ngIf="postResults.length > 0">
            <h2>Posty</h2>
            <div class="post-list">
              <div *ngFor="let post of postResults" class="post-item">
                <div class="post-header">
                  <div class="post-avatar">{{ post.user.username.charAt(0).toUpperCase() }}</div>
                  <div class="post-user-info">
                    <span class="post-user">{{ post.user.username }}</span>
                    <span class="post-date">{{ post.createdAt | date:'short' }}</span>
                  </div>
                </div>
                <div class="post-content">{{ post.content }}</div>
                <div *ngIf="post.imageUrl" class="post-media">
                  <img [src]="post.imageUrl" alt="Obrazek do posta" class="post-image" />
                </div>
              </div>
            </div>
          </div>
          <div *ngIf="!userResults.length && !postResults.length && !searching" class="explore-empty">Brak wyników.</div>
          <div *ngIf="searching" class="explore-loading">Szukam...</div>
        </div>
      </div>
    </app-layout>
  `,
  styleUrl: './explore.scss'
})
export class Explore {
  searchQuery = '';
  searching = false;
  postResults: Post[] = [];
  userResults: UserSearchResult[] = [];
  error: string | null = null;

  constructor(private postService: PostService, private userService: UserService) {}

  onSearch() {
    const query = this.searchQuery.trim();
    if (!query) {
      this.postResults = [];
      this.userResults = [];
      this.error = null;
      return;
    }
    this.searching = true;
    this.error = null;
    this.postService.searchPosts(query).subscribe({
      next: posts => {
        this.postResults = posts;
        this.searching = false;
      },
      error: () => {
        this.error = 'Błąd podczas wyszukiwania postów';
        this.searching = false;
      }
    });
    this.userService.searchUsers(query).subscribe({
      next: users => {
        this.userResults = users;
      },
      error: () => {
        this.error = 'Błąd podczas wyszukiwania użytkowników';
      }
    });
  }
} 