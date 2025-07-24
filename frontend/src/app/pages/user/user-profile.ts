import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { UserService, UserSearchResult, UserComment } from '../../services/user.service';
import { PostService, Post } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { Sidebar } from '../../components/sidebar/sidebar';
import { SidebarRight } from '../../components/sidebar-right/sidebar-right';

@Component({
  selector: 'app-user-profile',
  imports: [CommonModule, Sidebar, SidebarRight],
  templateUrl: './user-profile.html',
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
  isFollowLoading = false;

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
    const prev = this.isFollowing;
    this.isFollowLoading = true;
    if (this.isFollowing) {
      this.isFollowing = false;
      if (this.user) this.user.followersCount = (this.user.followersCount || 1) - 1;
      this.userService.unfollow(this.user.username).subscribe({
        next: () => { this.isFollowLoading = false; },
        error: () => {
          this.isFollowing = prev;
          if (this.user) this.user.followersCount = (this.user.followersCount || 0) + 1;
          this.isFollowLoading = false;
        }
      });
    } else {
      this.isFollowing = true;
      if (this.user) this.user.followersCount = (this.user.followersCount || 0) + 1;
      this.userService.follow(this.user.username).subscribe({
        next: () => { this.isFollowLoading = false; },
        error: () => {
          this.isFollowing = prev;
          if (this.user) this.user.followersCount = (this.user.followersCount || 1) - 1;
          this.isFollowLoading = false;
        }
      });
    }
  }

  getAvatarUrl(): string {
    return this.user?.avatar || 'assets/landing/logo-white.png';
  }
} 