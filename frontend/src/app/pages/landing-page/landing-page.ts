import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateAccount } from '../../components/create-account/create-account';
import { Login } from '../../components/login/login';

@Component({
  selector: 'app-landing-page',
  imports: [CommonModule, CreateAccount, Login],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss'
})
export class LandingPage {
  isCreateAccountModalOpen = false;
  isLoginModalOpen = false;

  openCreateAccountModal() {
    this.isCreateAccountModalOpen = true;
    document.body.style.overflow = 'hidden';
  }

  closeCreateAccountModal() {
    this.isCreateAccountModalOpen = false;
    document.body.style.overflow = 'auto';
  }

  openLoginModal() {
    this.isLoginModalOpen = true;
    document.body.style.overflow = 'hidden';
  }

  closeLoginModal() {
    this.isLoginModalOpen = false;
    document.body.style.overflow = 'auto';
  }

  switchToCreateAccount() {
    this.isLoginModalOpen = false;
    this.isCreateAccountModalOpen = true;
  }
}
