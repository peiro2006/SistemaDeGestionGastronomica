import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CarritoWidgetComponent } from './components/carrito-widget/carrito-widget';
import { CajaSelectorComponent } from './components/caja-selector/caja-selector';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CarritoWidgetComponent, CajaSelectorComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
