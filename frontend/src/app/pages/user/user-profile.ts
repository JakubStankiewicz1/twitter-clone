import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { UserService, UserSearchResult, UserComment } from '../../services/user.service';
import { PostService, Post } from '../../services/post.service';
import { AppLayout } from '../../components/app-layout/app-layout';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-profile',
  imports: [CommonModule, AppLayout],
  template: `
    <app-layout>
      <div class="user-profile-container" *ngIf="!loading && user; else loadingOrError">
        <div class="profile-header">
          <div class="profile-banner"></div>
          <div class="profile-avatar">
            <img [src]="getAvatarUrl()" alt="Avatar" />
          </div>
          <div class="profile-info">
            <h1 class="profile-name">{{ user.username }}</h1>
            <div class="profile-username">&#64;{{ user.username }}</div>
            <div class="profile-bio" *ngIf="user.bio">{{ user.bio }}</div>
            <div class="profile-meta">
              <span *ngIf="user.followersCount !== undefined">{{ user.followersCount }} followers</span>
              <span *ngIf="user.followingCount !== undefined">{{ user.followingCount }} following</span>
              <span *ngIf="user.createdAt">• Joined {{ user.createdAt | date:'MMMM yyyy' }}</span>
            </div>
            <button *ngIf="!isOwnProfile && isFollowing !== null" (click)="toggleFollow()" class="follow-btn">
              {{ isFollowing ? 'Unfollow' : 'Follow' }}
            </button>
          </div>
        </div>
        <div class="profile-posts">
          <div class="profile-tabs">
            <button [class.active]="tab === 'posts'" (click)="setTab('posts')">Posts</button>
            <button [class.active]="tab === 'replies'" (click)="setTab('replies')">Replies</button>
          </div>
          <ng-container *ngIf="tab === 'posts'">
            <div *ngIf="posts.length === 0" class="profile-empty">Brak postów.</div>
            <div *ngFor="let post of posts" class="profile-post">
              <div class="post-header">
                <div class="post-avatar">{{ user.username.charAt(0).toUpperCase() }}</div>
                <div class="post-user-info">
                  <span class="post-user">{{ user.username }}</span>
                  <span class="post-date">{{ post.createdAt | date:'short' }}</span>
                </div>
              </div>
              <div class="post-content">{{ post.content }}</div>
              <div *ngIf="post.imageUrl" class="post-media">
                <img [src]="post.imageUrl" alt="Obrazek do posta" class="post-image" />
              </div>
            </div>
          </ng-container>
          <ng-container *ngIf="tab === 'replies'">
            <div *ngIf="replies.length === 0" class="profile-empty">Brak odpowiedzi.</div>
            <div *ngFor="let reply of replies" class="profile-post">
              <div class="post-header">
                <div class="post-avatar">{{ reply.username.charAt(0).toUpperCase() }}</div>
                <div class="post-user-info">
                  <span class="post-user">{{ reply.username }}</span>
                  <span class="post-date">{{ reply.createdAt | date:'short' }}</span>
                </div>
              </div>
              <div class="post-content">{{ reply.content }}</div>
            </div>
          </ng-container>
        </div>
      </div>
      <ng-template #loadingOrError>
        <div *ngIf="loading" class="profile-loading">Ładowanie profilu...</div>
        <div *ngIf="error" class="profile-error">{{ error }}</div>
      </ng-template>
    </app-layout>
  `,
  styleUrl: './user-profile.scss'
})
export class UserProfile implements OnInit {
  username: string = '';
  user: UserSearchResult | null = null;
  posts: Post[] = [];
  replies: UserComment[] = [];
  loading = true;
  error: string | null = null;
  tab: 'posts' | 'replies' = 'posts';
  isFollowing: boolean | null = null;
  isOwnProfile = false;
  currentUser: any = null;

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private postService: PostService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.loadProfile();
    });
  }

  loadProfile() {
    this.route.paramMap.subscribe(params => {
      this.username = params.get('username') || '';
      if (this.username) {
        this.userService.getUserByUsername(this.username).subscribe({
          next: user => {
            this.user = user;
            this.loading = false;
            this.isOwnProfile = this.currentUser && user.username === this.currentUser.username;
            if (!this.isOwnProfile && this.currentUser) {
              this.userService.getFollowers(user.username).subscribe(followers => {
                this.isFollowing = followers.includes(this.currentUser.username);
              });
            } else {
              this.isFollowing = null;
            }
            this.fetchPosts();
            this.fetchReplies();
          },
          error: () => {
            this.error = 'Nie znaleziono użytkownika';
            this.loading = false;
          }
        });
      }
    });
  }

  setTab(tab: 'posts' | 'replies') {
    this.tab = tab;
  }

  fetchPosts() {
    if (!this.user) return;
    this.postService.getPostsByUserId(this.user.id).subscribe({
      next: posts => this.posts = posts,
      error: () => this.posts = []
    });
  }

  fetchReplies() {
    if (!this.user) return;
    this.userService.getUserComments(this.user.username).subscribe({
      next: replies => this.replies = replies,
      error: () => this.replies = []
    });
  }

  toggleFollow() {
    if (!this.user || !this.currentUser) return;
    if (this.isFollowing) {
      this.userService.unfollow(this.user.username).subscribe(() => {
        this.isFollowing = false;
        if (this.user) this.user.followersCount = (this.user.followersCount || 1) - 1;
      });
    } else {
      this.userService.follow(this.user.username).subscribe(() => {
        this.isFollowing = true;
        if (this.user) this.user.followersCount = (this.user.followersCount || 0) + 1;
      });
    }
  }

  getAvatarUrl(): string {
    return this.user?.avatar || 'assets/landing/logo-white.png';
  }
} 