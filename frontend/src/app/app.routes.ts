import { Routes } from '@angular/router';
import { LandingPage } from './landing-page/landing-page';
import { Home } from './home/home';
import { authGuard } from './guards/auth.guard';
import { Messages } from './components/messages/messages';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        loadComponent: () => {
            return import('./landing-page/landing-page').then(
                    m => m.LandingPage
            );
        }

    },
    {
        path: 'home',
        loadComponent: () => {
            return import('./home/home').then(
                m => m.Home
            )
        },
        canActivate: [authGuard]
    },
    {
        path: 'messages',
        loadComponent: () => import('./components/messages/messages').then(m => m.Messages),
        canActivate: [authGuard]
    }
];
