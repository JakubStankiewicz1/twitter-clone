import { Component, Input } from '@angular/core';
import { Sidebar } from '../sidebar/sidebar';
import { SidebarRight } from '../sidebar-right/sidebar-right';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [Sidebar, SidebarRight],
  template: `
    <div class="home">
      <app-sidebar class="home_one" />
      <div class="home_two">
        <ng-content></ng-content>
      </div>
      <app-sidebar-right class="home_three" />
    </div>
  `,
  styleUrl: '../../pages/home/home.scss'
})
export class AppLayout {} 