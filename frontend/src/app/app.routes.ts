import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        loadComponent: () => {
            return import('./pages/landing-page/landing-page').then(
                    m => m.LandingPage
            );
        }

    },
    {
        path: 'home',
        loadComponent: () => {
            return import('./pages/home/home').then(
                m => m.Home
            )
        },
        canActivate: [authGuard]
    },
    {
        path: 'messages',
        loadComponent: () => import('./pages/messages/messages').then(m => m.Messages),
        canActivate: [authGuard]
    },
    {
        path: 'explore',
        loadComponent: () => import('./pages/explore/explore').then(m => m.Explore),
        canActivate: [authGuard]
    },
    {
        path: 'user/:username',
        loadComponent: () => import('./pages/user/user-profile').then(m => m.UserProfile)
    },
    {
        path: 'post/:id',
        loadComponent: () => import('./pages/post/post-page').then(m => m.PostPage)
    },
    {
        path: 'profile',
        loadComponent: () => import('./pages/profile/profile').then(m => m.Profile),
        canActivate: [authGuard]
    }
];
