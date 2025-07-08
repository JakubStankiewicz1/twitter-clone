import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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
    birthMonth: '',
    birthDay: '',
    birthYear: ''
  };

  months = [
    'Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec',
    'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'
  ];

  days = Array.from({length: 31}, (_, i) => i + 1);
  years = Array.from({length: 100}, (_, i) => new Date().getFullYear() - i);

  onClose() {
    this.closeModal.emit();
  }

  onSubmit() {
    console.log('Formularz wysłany:', this.formData);
    // Tutaj będzie logika rejestracji
  }

  isFormValid(): boolean {
    return this.formData.name.length >= 2 && 
           this.formData.email.includes('@') &&
           !!this.formData.birthMonth &&
           !!this.formData.birthDay &&
           !!this.formData.birthYear;
  }
}
