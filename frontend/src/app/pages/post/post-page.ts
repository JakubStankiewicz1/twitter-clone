import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PostService, Post } from '../../services/post.service';
import { UserService, UserSearchResult } from '../../services/user.service';
import { Sidebar } from '../../components/sidebar/sidebar';
import { SidebarRight } from '../../components/sidebar-right/sidebar-right';
import { Location } from '@angular/common';

@Component({
  selector: 'app-post-page',
  imports: [CommonModule, FormsModule, Sidebar, SidebarRight],
  templateUrl: './post-page.html',
  styleUrl: './post-page.scss'
})
export class PostPage implements OnInit {
  post: Post | null = null;
  comments: any[] = [];
  loading = true;
  error: string | null = null;
  newComment = '';
  addingComment = false;
  liked = false;
  likesCount = 0; // TODO: pobierz z backendu, jeśli będzie dostępne

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private userService: UserService,
    private location: Location
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
        if (!this.post) {
          this.error = 'Nie znaleziono posta.';
        } else {
          this.likesCount = 0; // Brak pola likesCount w Post, domyślnie 0
          this.liked = false; // Możesz tu dodać logikę sprawdzania czy user już polubił
        }
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

  goBack() {
    this.location.back();
  }

  toggleLike() {
    if (!this.post) return;
    this.liked = !this.liked;
    this.likesCount += this.liked ? 1 : -1;
    // Tu możesz dodać wywołanie serwisu do backendu (optymistycznie)
    // this.postService.likePost(this.post.id).subscribe(...)
  }

  parseContent(content: string): string {
    try {
      const obj = JSON.parse(content);
      return obj.content || content;
    } catch {
      return content;
    }
  }
} 