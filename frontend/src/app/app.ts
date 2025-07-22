import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  template: `
    <router-outlet />
  `,
  styles: [`
    :host {
      display: block;
      height: 100vh;
      width: 100%;
      margin: 0;
      padding: 0;
    }
  `],
})
export class App {
  protected title = 'twitter-clone';
}