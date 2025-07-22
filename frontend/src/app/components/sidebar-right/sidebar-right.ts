import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService, UserSearchResult } from '../../services/user.service';
import { AuthService, UserResponse } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-sidebar-right',
  imports: [CommonModule, FormsModule],
  templateUrl: './sidebar-right.html',
  styleUrl: './sidebar-right.scss'
})
export class SidebarRight {
  searchQuery = '';
  searchResults: UserSearchResult[] = [];
  loading = false;
  currentUser: UserResponse | null = null;
  private searchSubject = new Subject<string>();
  followingMap: { [userId: number]: boolean } = {};

  trendsData = [
    {
      category: 'NFL',
      title: 'NFL Top 100 Countdown',
      status: 'LIVE',
      image: 'https://images.unsplash.com/photo-1508162942367-e4b4b7e4b4e4?w=60&h=60&fit=crop'
    }
  ];

  constructor(private userService: UserService, private authService: AuthService) {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      this.performSearch(query);
    });
  }

  onSearchChange(query: string) {
    this.searchSubject.next(query);
  }

  performSearch(query: string) {
    if (!query.trim()) {
      this.searchResults = [];
      return;
    }
    this.loading = true;
    this.userService.searchUsers(query).subscribe(users => {
      this.searchResults = users.filter(u => !this.currentUser || u.id !== this.currentUser.id);
      this.loading = false;
      // Sprawdź follow dla każdego usera
      if (this.currentUser) {
        this.searchResults.forEach(user => {
          this.userService.getFollowers(user.username).subscribe((followers: string[]) => {
            this.followingMap[user.id] = followers.includes(this.currentUser!.username);
          });
        });
      }
    }, () => this.loading = false);
  }

  toggleFollow(user: UserSearchResult) {
    if (!this.currentUser) return;
    if (this.followingMap[user.id]) {
      this.userService.unfollow(user.username).subscribe((res: any) => {
        this.followingMap[user.id] = false;
      });
    } else {
      this.userService.follow(user.username).subscribe((res: any) => {
        this.followingMap[user.id] = true;
      });
    }
  }
}
