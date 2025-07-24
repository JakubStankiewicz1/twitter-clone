import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PostService, Post } from '../../services/post.service';
import { UserService, UserSearchResult } from '../../services/user.service';
import { Sidebar } from '../../components/sidebar/sidebar';
import { SidebarRight } from '../../components/sidebar-right/sidebar-right';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-explore',
  imports: [CommonModule, FormsModule, RouterModule, Sidebar, SidebarRight],
  templateUrl: './explore.html',
  styleUrl: './explore.scss'
})
export class Explore {
  searchQuery = '';
  searching = false;
  postResults: Post[] = [];
  userResults: UserSearchResult[] = [];
  error: string | null = null;
  private searchSubject = new Subject<string>();

  constructor(private postService: PostService, private userService: UserService) {
    this.searchSubject.pipe(debounceTime(300)).subscribe(query => {
      this.doSearch(query);
    });
  }

  onSearch() {
    this.searchSubject.next(this.searchQuery.trim());
  }

  private doSearch(query: string) {
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