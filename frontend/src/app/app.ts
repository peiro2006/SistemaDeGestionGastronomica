import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CarritoWidgetComponent } from './components/carrito-widget/carrito-widget';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CarritoWidgetComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
