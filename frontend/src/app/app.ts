import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  template: `
    <div class="app-layout">
      <div class="app-content">
        <router-outlet />
      </div>
    </div>
  `,
  styles: [`
    .app-layout {
      display: flex;
      height: 100vh;
      width: 100vw;
      margin: 0;
      padding: 0;
      background: #000;
    }
    .app-content {
      flex: 1 1 auto;
      min-width: 0;
      height: 100vh;
      overflow-y: auto;
      background: #000;
    }
  `],
})
export class App {
  protected title = 'twitter-clone';
}