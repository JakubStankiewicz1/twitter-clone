import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  @Output() closeModal = new EventEmitter<void>();
  @Output() openCreateAccount = new EventEmitter<void>();

  formData = {
    identifier: ''
  };

  onClose() {
    this.closeModal.emit();
  }

  onSubmit() {
    console.log('Logowanie:', this.formData);
    // Tutaj będzie logika logowania
  }

  onSignUpClick(event: Event) {
    event.preventDefault();
    this.closeModal.emit();
    this.openCreateAccount.emit();
  }

  isFormValid(): boolean {
    return this.formData.identifier.trim().length > 0;
  }
}
