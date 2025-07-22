import { Component, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login {
  @Output() closeModal = new EventEmitter<void>();
  @Output() openCreateAccount = new EventEmitter<void>();

  formData = {
    identifier: '',
    password: ''
  };

  isLoading = false;
  errorMessage = '';
  currentStep = 1; // 1 - email/username, 2 - password

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onClose() {
    this.closeModal.emit();
  }

  nextStep() {
    if (this.formData.identifier.trim()) {
      this.currentStep = 2;
    }
  }

  prevStep() {
    this.currentStep = 1;
    this.errorMessage = '';
  }

  onSubmit() {
    if (!this.isFormValid()) {
      this.errorMessage = 'Proszę wypełnić wszystkie pola';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const loginData: LoginRequest = {
      username: this.formData.identifier.trim(),
      password: this.formData.password
    };

    this.authService.login(loginData).subscribe({
      next: (response) => {
        console.log('Logowanie pomyślne:', response);
        this.closeModal.emit();
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Błąd logowania:', error);
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message.replace('Error: ', '');
        } else {
          this.errorMessage = 'Wystąpił błąd podczas logowania. Spróbuj ponownie.';
        }
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  onSignUpClick(event: Event) {
    event.preventDefault();
    this.closeModal.emit();
    this.openCreateAccount.emit();
  }

  isFormValid(): boolean {
    return this.formData.identifier.trim().length > 0 && 
           this.formData.password.length >= 6;
  }
}
