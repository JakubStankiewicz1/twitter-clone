import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, RegisterRequest } from '../../services/auth.service';

@Component({
  selector: 'app-create-account',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-account.html',
  styleUrl: './create-account.scss'
})
export class CreateAccount {
  @Output() closeModal = new EventEmitter<void>();

  formData = {
    name: '',
    email: '',
    username: '',
    password: '',
    birthMonth: '',
    birthDay: '',
    birthYear: ''
  };

  isLoading = false;
  errorMessage = '';
  currentStep = 1; // 1 - dane podstawowe, 2 - hasło i username

  months = [
    'Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec',
    'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'
  ];

  days = Array.from({length: 31}, (_, i) => i + 1);
  years = Array.from({length: 100}, (_, i) => new Date().getFullYear() - i);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onClose() {
    this.closeModal.emit();
  }

  nextStep() {
    if (this.isStep1Valid()) {
      this.currentStep = 2;
    }
  }

  prevStep() {
    this.currentStep = 1;
  }

  onSubmit() {
    if (!this.isFormValid()) {
      this.errorMessage = 'Proszę wypełnić wszystkie pola poprawnie';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const registerData: RegisterRequest = {
      username: this.formData.username,
      email: this.formData.email,
      password: this.formData.password,
      displayName: this.formData.name
    };

    this.authService.register(registerData).subscribe({
      next: (response) => {
        console.log('Rejestracja pomyślna:', response);
        this.closeModal.emit();
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Błąd rejestracji:', error);
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message.replace('Error: ', '');
        } else {
          this.errorMessage = 'Wystąpił błąd podczas rejestracji. Spróbuj ponownie.';
        }
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  isStep1Valid(): boolean {
    return this.formData.name.length >= 2 && 
           this.formData.email.includes('@') &&
           !!this.formData.birthMonth &&
           !!this.formData.birthDay &&
           !!this.formData.birthYear;
  }

  isFormValid(): boolean {
    return this.isStep1Valid() &&
           this.formData.username.length >= 3 &&
           this.formData.password.length >= 6;
  }
}
