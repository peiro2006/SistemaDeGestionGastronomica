import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { RegistroComponent } from './pages/registro/registro';
import { HomeComponent } from './pages/home/home';
import { CatalogoComponent } from './pages/catalogo/catalogo';
import { AdminProductosComponent } from './pages/admin-productos/admin-productos';
import { AdminStockComponent } from './pages/admin-stock/admin-stock';
import { MisPedidosComponent } from './pages/mis-pedidos/mis-pedidos';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/catalogo', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'catalogo', component: CatalogoComponent },
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  { path: 'mis-pedidos', component: MisPedidosComponent, canActivate: [authGuard] },
  { path: 'admin/productos', component: AdminProductosComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/stock', component: AdminStockComponent, canActivate: [authGuard, adminGuard] },
  { path: '**', redirectTo: '/catalogo' }
];
