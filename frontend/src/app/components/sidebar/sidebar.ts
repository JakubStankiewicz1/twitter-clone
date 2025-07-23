import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService, UserResponse } from '../../services/auth.service';
import { PostModal } from './post-modal';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterModule, PostModal],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar implements OnInit, OnDestroy {
  currentUser: UserResponse | null = null;
  private userSubscription?: Subscription;
  showPostModal = false;
  @Output() postAdded = new EventEmitter<void>();
  showUserMenu = false;

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
      this.authService.getProfile().subscribe({
        next: (user: UserResponse) => {
          this.authService.setCurrentUser(user); // Ustawiamy currentUser na podstawie backendu
          console.log('Pobrany użytkownik:', user);
        },
        error: (error: any) => {
          console.error('Błąd pobierania użytkownika:', error);
          // Jeżeli token jest nieprawidłowy, wyloguj użytkownika
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
  toggleUserMenu(event: Event) {
    event.stopPropagation();
    this.showUserMenu = !this.showUserMenu;
    if (this.showUserMenu) {
      setTimeout(() => {
        window.addEventListener('click', this.closeUserMenu);
      });
    }
  }

  closeUserMenu = () => {
    this.showUserMenu = false;
    window.removeEventListener('click', this.closeUserMenu);
  };

  addAccount() {
    alert('Add account (not implemented yet)');
    this.showUserMenu = false;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
    this.showUserMenu = false;
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
