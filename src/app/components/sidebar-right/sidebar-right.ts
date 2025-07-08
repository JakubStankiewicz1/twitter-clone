import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar-right',
  imports: [CommonModule],
  templateUrl: './sidebar-right.html',
  styleUrl: './sidebar-right.scss'
})
export class SidebarRight {
  whoToFollowUsers = [
    {
      name: 'Jarosław Wolski',
      handle: '@wolski_jaros',
      verified: true,
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=40&h=40&fit=crop&crop=face'
    },
    {
      name: 'ethanweng',
      handle: '@ethanwengg',
      verified: true,
      avatar: 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=40&h=40&fit=crop&crop=face'
    },
    {
      name: 'Volodymyr Zelenskyy',
      handle: '@ZelenskyyUa',
      verified: true,
      avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop&crop=face'
    }
  ];

  trendsData = [
    {
      category: 'NFL',
      title: 'NFL Top 100 Countdown',
      status: 'LIVE',
      image: 'https://images.unsplash.com/photo-1508162942367-e4b4b7e4b4e4?w=60&h=60&fit=crop'
    }
  ];

  followUser(user: any) {
    console.log('Following user:', user.name);
  }
}
