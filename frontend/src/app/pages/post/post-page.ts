import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PostService, Post } from '../../services/post.service';
import { UserService, UserSearchResult } from '../../services/user.service';
import { AppLayout } from '../../components/app-layout/app-layout';

@Component({
  selector: 'app-post-page',
  imports: [CommonModule, FormsModule, AppLayout],
  template: `
    <app-layout>
      <div class="post-page-container">
        <ng-container *ngIf="loading">Ładowanie posta...</ng-container>
        <ng-container *ngIf="error">{{ error }}</ng-container>
        <ng-container *ngIf="!loading && post">
          <div class="post-card">
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
            <div class="post-actions">
              <!-- Możesz dodać obsługę lajków tutaj w przyszłości -->
            </div>
          </div>
          <div class="comments-section">
            <h2>Komentarze</h2>
            <form class="add-comment-form" (ngSubmit)="addComment()">
              <textarea [(ngModel)]="newComment" name="newComment" placeholder="Dodaj komentarz..." rows="2" [disabled]="addingComment"></textarea>
              <button type="submit" [disabled]="!newComment.trim() || addingComment">Dodaj</button>
            </form>
            <div *ngIf="comments.length === 0" class="empty-comments">Brak komentarzy.</div>
            <div class="comment-list">
              <div *ngFor="let comment of comments" class="comment-item">
                <div class="comment-avatar">{{ comment.username.charAt(0).toUpperCase() }}</div>
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="comment-user">{{ comment.username }}</span>
                    <span class="comment-date">{{ comment.createdAt | date:'short' }}</span>
                  </div>
                  <div class="comment-content">{{ comment.content }}</div>
                </div>
              </div>
            </div>
          </div>
        </ng-container>
      </div>
    </app-layout>
  `,
  styleUrl: './post-page.scss'
})
export class PostPage implements OnInit {
  post: Post | null = null;
  comments: any[] = [];
  loading = true;
  error: string | null = null;
  newComment = '';
  addingComment = false;

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const postId = params.get('id');
      if (postId) {
        this.loadPost(+postId);
        this.loadComments(+postId);
      }
    });
  }

  loadPost(postId: number) {
    this.loading = true;
    this.error = null;
    this.postService.getFeed().subscribe({
      next: posts => {
        this.post = posts.find(p => p.id === postId) || null;
        this.loading = false;
        if (!this.post) this.error = 'Nie znaleziono posta.';
      },
      error: () => {
        this.error = 'Błąd podczas ładowania posta.';
        this.loading = false;
      }
    });
  }

  loadComments(postId: number) {
    this.postService.getComments(postId).subscribe({
      next: comments => {
        this.comments = comments;
      },
      error: () => {
        this.comments = [];
      }
    });
  }

  addComment() {
    if (!this.newComment.trim() || !this.post) return;
    this.addingComment = true;
    this.postService.addComment(this.post.id, this.newComment.trim()).subscribe({
      next: comment => {
        this.comments.push(comment);
        this.newComment = '';
        this.addingComment = false;
      },
      error: () => {
        this.addingComment = false;
        alert('Błąd podczas dodawania komentarza.');
      }
    });
  }
} 