import { Component, EventEmitter, Output } from '@angular/core';
import { PostService } from '../../services/post.service';
import { AuthService, UserResponse } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-post-modal',
  templateUrl: './post-modal.html',
  styleUrl: './post-modal.scss',
  standalone: true,
  imports: [FormsModule]
})
export class PostModal {
  @Output() close = new EventEmitter<void>();
  @Output() postAdded = new EventEmitter<void>();
  content = '';
  mediaUrl = '';
  mediaAlt = '';
  adding = false;
  user: UserResponse | null = null;

  constructor(private postService: PostService, private authService: AuthService) {
    this.authService.currentUser$.subscribe(user => this.user = user);
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name.charAt(0).toUpperCase();
  }

  submit() {
    if (!this.user || !this.content.trim()) return;
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
        this.postAdded.emit();
      },
      error: () => {
        this.adding = false;
      }
    });
  }
}
