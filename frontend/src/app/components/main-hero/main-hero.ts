import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe, CommonModule } from '@angular/common';
import { PostService, Post } from '../../services/post.service';
import { AuthService, UserResponse } from '../../services/auth.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-main-hero',
  imports: [CommonModule, FormsModule, DatePipe, RouterModule],
  templateUrl: './main-hero.html',
  styleUrl: './main-hero.scss'
})
export class MainHero implements OnInit {
  posts: Post[] = [];
  loading = true;
  error: string | null = null;
  content: string = '';
  mediaUrl: string = '';
  mediaAlt: string = '';
  user: UserResponse | null = null;
  adding = false;
  tab: 'foryou' | 'following' = 'foryou';

  constructor(private postService: PostService, private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => this.user = user);
    this.setTab('foryou');
  }

  setTab(tab: 'foryou' | 'following') {
    this.tab = tab;
    this.loadPosts();
  }

  loadPosts() {
    this.loading = true;
    this.error = null;
    if (this.tab === 'foryou') {
      this.postService.getFeed().subscribe({
        next: (posts: Post[]) => {
          this.posts = posts;
          this.loading = false;
        },
        error: (err: any) => {
          this.error = 'Błąd podczas pobierania postów';
          this.loading = false;
        }
      });
    } else if (this.tab === 'following' && this.user) {
      this.postService.getUserPosts(this.user.id).subscribe({
        next: (posts: Post[]) => {
          this.posts = posts;
          this.loading = false;
        },
        error: (err: any) => {
          this.error = 'Błąd podczas pobierania postów';
          this.loading = false;
        }
      });
    } else {
      this.posts = [];
      this.loading = false;
    }
  }

  addPost() {
    if (!this.user) return;
    this.adding = true;
    const post: any = {
      userId: this.user.id,
      content: this.content
    };
    if (this.mediaUrl) {
      post.mediaUrls = [{ url: this.mediaUrl, type: 'image', altText: this.mediaAlt }];
    }
    this.postService.addPost(post).subscribe({
      next: () => {
        this.content = '';
        this.mediaUrl = '';
        this.mediaAlt = '';
        this.adding = false;
        this.loadPosts();
      },
      error: () => {
        this.error = 'Błąd podczas dodawania posta';
        this.adding = false;
      }
    });
  }
}
