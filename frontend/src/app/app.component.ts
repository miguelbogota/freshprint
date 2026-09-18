import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/** Root shell; feature content is loaded by the router. */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {}
