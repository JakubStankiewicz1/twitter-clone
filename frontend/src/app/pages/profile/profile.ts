import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppLayout } from '../../components/app-layout/app-layout';
import { UserService, UserComment } from '../../services/user.service';
import { PostService, Post } from '../../services/post.service';
import { UserResponse } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, AppLayout],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class Profile implements OnInit {
  user: UserResponse | null = null;
  posts: Post[] = [];
  replies: UserComment[] = [];
  loading = true;
  error: string | null = null;
  tab: 'posts' | 'replies' = 'posts';
  showEditModal = false;
  editBio = '';
  editAvatar = '';
  editPassword = '';
  followersCount = 0;
  followingCount = 0;

  constructor(
    private userService: UserService,
    private postService: PostService
  ) {}

  ngOnInit() {
    this.userService.getOwnProfile().subscribe({
      next: user => {
        this.user = user;
        this.editBio = user.bio || '';
        this.editAvatar = user.avatar || '';
        this.fetchPosts();
        this.fetchReplies();
        this.userService.getFollowCounts(user.username).subscribe(counts => {
          this.followersCount = counts.followers;
          this.followingCount = counts.following;
        });
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load profile.';
        this.loading = false;
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

  getAvatarUrl(): string {
    return this.user?.avatar || 'assets/landing/logo-white.png';
  }

  openEditModal() {
    this.showEditModal = true;
  }

  closeEditModal() {
    this.showEditModal = false;
  }

  saveProfile() {
    if (!this.user) return;
    this.userService
      .updateProfile({
        bio: this.editBio,
        avatar: this.editAvatar,
        password: this.editPassword || undefined
      })
      .subscribe({
        next: (updated: UserResponse) => {
          this.user = { ...this.user!, ...updated };
          this.closeEditModal();
        },
        error: () => {
          alert('Error updating profile');
        }
      });
  }

  editPost(post: Post) {
    alert('Edit post (not implemented yet)');
  }

  deletePost(post: Post) {
    if (!confirm('Delete this post?')) return;
    this.postService.deletePost(post.id).subscribe(() => {
      this.posts = this.posts.filter(p => p.id !== post.id);
    });
  }
} 