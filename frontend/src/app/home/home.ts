import { Component } from '@angular/core';
import { Sidebar } from '../components/sidebar/sidebar';
import { SidebarRight } from "../components/sidebar-right/sidebar-right";
import { MainHero } from "../components/main-hero/main-hero";

@Component({
  selector: 'app-home',
  imports: [Sidebar, SidebarRight, MainHero],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {

}
