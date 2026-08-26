import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'login',
    renderMode: RenderMode.Client
  },
  {
    path: 'registro',
    renderMode: RenderMode.Client
  },
  {
    path: 'home',
    renderMode: RenderMode.Client
  },
  {
    path: 'catalogo',
    renderMode: RenderMode.Client
  },
  {
    path: 'mis-pedidos',
    renderMode: RenderMode.Client
  },
  {
    path: 'admin/productos',
    renderMode: RenderMode.Client
  },
  {
    path: 'admin/stock',
    renderMode: RenderMode.Client
  },
  {
    path: '**',
    renderMode: RenderMode.Client
  }
];
