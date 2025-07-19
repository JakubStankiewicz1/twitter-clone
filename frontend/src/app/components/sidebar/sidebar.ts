import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService, UserResponse } from '../../services/auth.service';
import { PostModal } from './post-modal';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, PostModal],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar implements OnInit, OnDestroy {
  currentUser: UserResponse | null = null;
  private userSubscription?: Subscription;
  showPostModal = false;
  @Output() postAdded = new EventEmitter<void>();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    // Subskrybuj zmiany użytkownika
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    // Jeśli użytkownik jest zalogowany, pobierz aktualne dane
    if (this.authService.isLoggedIn()) {
      this.authService.getCurrentUser().subscribe({
        next: (user) => {
          console.log('Pobrany użytkownik:', user);
        },
        error: (error) => {
          console.error('Błąd pobierania użytkownika:', error);
          // Jeśli token jest nieprawidłowy, wyloguj użytkownika
          this.authService.logout();
          this.router.navigate(['/']);
        }
      });
    }
  }

  ngOnDestroy() {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  // Usunięto duplikaty funkcji openPostModal, closePostModal, onPostAdded
  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  openPostModal() {
    this.showPostModal = true;
  }

  closePostModal() {
    this.showPostModal = false;
  }

  onPostAdded() {
    this.closePostModal();
    this.postAdded.emit();
  }

  getInitials(displayName: string): string {
    if (!displayName) return '?';
    return displayName.charAt(0).toUpperCase();
  }
}
