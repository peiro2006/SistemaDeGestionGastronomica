import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface NavGroup {
  label: string;
  items: { label: string; route: string }[];
}

@Component({
  selector: 'app-admin-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './admin-sidebar.html',
  styleUrl: './admin-sidebar.css'
})
export class AdminSidebarComponent {
  collapsed = signal(false);
  openGroups = signal<Record<string, boolean>>({
    'Carta y Cocina': true,
    'Finanzas / Libro Contable': true,
    'Abastecimiento': true,
    'Inteligencia': true
  });

  groups: NavGroup[] = [
    {
      label: 'Carta y Cocina',
      items: [
        { label: 'Productos', route: '/admin/productos' },
        { label: 'Stock', route: '/admin/stock' },
        { label: 'Pedidos', route: '/empleado/pedidos' },
      ]
    },
    {
      label: 'Finanzas / Libro Contable',
      items: [
        { label: 'Cajas', route: '/admin/cajas' },
      ]
    },
    {
      label: 'Abastecimiento',
      items: [
        { label: 'Proveedores', route: '/admin/proveedores' },
      ]
    },
    {
      label: 'Inteligencia',
      items: [
        { label: 'KPI Dashboard', route: '/kpi' },
        { label: 'Reportes', route: '/admin/reportes' },
        { label: 'Feedback', route: '/admin/feedback' },
      ]
    }
  ];

  toggleGroup(key: string): void {
    this.openGroups.update(v => ({ ...v, [key]: !v[key] }));
  }
  isOpen(key: string): boolean { return this.openGroups()[key] ?? true; }
  toggleSidebar(): void { this.collapsed.update(v => !v); }
}
