import { Component, OnInit, inject, signal, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { BaseResponse } from '../../models/auth.models';
import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar';

Chart.register(...registerables);

interface KpiData {
  totalPedidos: number; ingresosTotales: number; promedioPedido: number;
  totalProductos: number; productosActivos: number; productosInactivos: number;
  stockTotal: number; stockBajo: number;
  pedidosPorEstado: Record<string, number>; tasaExito: number; tasaCancelacion: number;
  ingresos7Dias: number; ingresos30Dias: number; ingresos90Dias: number;
  pedidos7Dias: number; pedidos30Dias: number; pedidos90Dias: number;
  tendenciaDiaria: { fecha:string; label:string; ingresos:number; pedidos:number }[];
  tendenciaSemanal: { fecha:string; label:string; ingresos:number; pedidos:number }[];
  tasaConversion: number; cac: number; cac30: number;
  traficoWeb30: number; traficoWeb7: number; ctr: number; ctr7: number;
  margenBeneficio: number; margen30: number; roi: number;
  ingresosPorMetodoPago: Record<string, number>; pedidosPorMetodoPago: Record<string, number>;
  stockPorCategoria: Record<string, number>; productosPorCategoria: Record<string, number>;
  productosPorProveedor: Record<string, number>; stockPorProveedor: Record<string, number>;
  totalProveedores: number; proveedoresActivos: number;
  topProductos: { nombre:string; cantidad:number; ingresos:number }[];
  usuariosPorRol: Record<string, number>; totalUsuarios: number; tasaRetencion: number; tasaRotacion: number;
  metaMensual: number; cumplimientoMeta: number; periodo: string; timestamp: string;
}

@Component({
  selector: 'app-kpi-dashboard',
  imports: [CommonModule, RouterLink, RouterLinkActive, AdminSidebarComponent],
  templateUrl: './kpi-dashboard.html',
  styleUrl: './kpi-dashboard.css'
})
export class KpiDashboardComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  @ViewChild('estadoChart') estadoChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('ingresosChart') ingresosChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('tendenciaChart') tendenciaChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('metodoPagoChart') metodoPagoChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('stockCatChart') stockCatChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('topProdChart') topProdChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('proveedorChart') proveedorChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('usuariosChart') usuariosChartRef!: ElementRef<HTMLCanvasElement>;

  private charts: Chart[] = [];

  readonly kpis = signal<KpiData | null>(null);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  estados: { estado:string; count:number }[] = [];

  ngOnInit(): void { this.cargarKPIs(); }

  cargarKPIs(): void {
    this.cargando.set(true);
    this.http.get<BaseResponse<KpiData>>(`${this.apiUrl}/kpi`).subscribe({
      next: (res) => {
        const data = (res as any).data ?? res;
        // unwrap BaseResponse
        const k = (data as KpiData).ingresosTotales !== undefined ? (data as KpiData) : (res.data as KpiData);
        this.kpis.set(k);
        this.estados = Object.entries(k.pedidosPorEstado||{}).map(([estado,count])=>({estado, count: count as number}));
        this.cargando.set(false);
        setTimeout(()=>this.crearGraficos(),150);
      },
      error: (err) => {
        const httpError = err as { error?: { message?: string; errors?: string[] } };
        const msg = httpError?.error?.errors?.join(' - ') ?? httpError?.error?.message ?? 'No se pudieron cargar los KPIs';
        this.error.set(msg); this.cargando.set(false);
      }
    });
  }

  private crearGraficos(): void {
    const d = this.kpis(); if (!d) return;
    this.charts.forEach(c=>c.destroy()); this.charts=[];
    this.crearEstadoChart(); this.crearIngresosChart(); this.crearTendenciaChart();
    this.crearMetodoPagoChart(); this.crearStockCatChart(); this.crearTopProdChart();
    this.crearProveedorChart(); this.crearUsuariosChart();
  }

  private crearEstadoChart(): void {
    const ctx = this.estadoChartRef?.nativeElement.getContext('2d'); if (!ctx) return;
    const labels = this.estados.map(e=>this.etiquetaEstado(e.estado));
    const values = this.estados.map(e=>e.count);
    const colors = this.estados.map(e=>this.estadoColor(e.estado));
    const c = new Chart(ctx, { type:'bar', data:{ labels, datasets:[{ label:'Pedidos', data:values, backgroundColor:colors, borderRadius:6 }] }, options:{ responsive:true, maintainAspectRatio:false, plugins:{ legend:{ display:false } }, scales:{ y:{ beginAtZero:true, ticks:{ stepSize:1 } } } } } as ChartConfiguration);
    this.charts.push(c);
  }
  private crearIngresosChart(): void {
    const ctx = this.ingresosChartRef?.nativeElement.getContext('2d'); if (!ctx || !this.kpis()) return;
    const k=this.kpis()!; const c=new Chart(ctx,{ type:'bar', data:{ labels:['Últimos 7 días','Últimos 30 días','Últimos 90 días'], datasets:[{ label:'Ingresos ARS', data:[k.ingresos7Dias,k.ingresos30Dias,k.ingresos90Dias], backgroundColor:['#3b82f6','#10b981','#8b5cf6'], borderRadius:6 },{ label:'Pedidos', data:[k.pedidos7Dias,k.pedidos30Dias,k.pedidos90Dias], backgroundColor:'rgba(245,158,11,0.85)', borderRadius:6 }]}, options:{ responsive:true, maintainAspectRatio:false, plugins:{ legend:{ position:'bottom'}}, scales:{ y:{ beginAtZero:true } } } } as ChartConfiguration); this.charts.push(c);
  }
  private crearTendenciaChart(): void {
    const ctx=this.tendenciaChartRef?.nativeElement.getContext('2d'); if (!ctx||!this.kpis()) return;
    const k=this.kpis()!; const labels=k.tendenciaDiaria.map(t=>t.label); const ingresos=k.tendenciaDiaria.map(t=>Number(t.ingresos)); const pedidos=k.tendenciaDiaria.map(t=>t.pedidos);
    const c=new Chart(ctx,{ type:'line', data:{ labels, datasets:[{ label:'Ingresos', data:ingresos, borderColor:'#3b82f6', backgroundColor:'rgba(59,130,246,0.15)', fill:true, tension:0.35, yAxisID:'y' },{ label:'Pedidos', data:pedidos, borderColor:'#f59e0b', backgroundColor:'rgba(245,158,11,0.15)', fill:false, tension:0.35, yAxisID:'y1' }]}, options:{ responsive:true, maintainAspectRatio:false, interaction:{ mode:'index', intersect:false }, plugins:{ legend:{ position:'bottom'}}, scales:{ y:{ type:'linear', position:'left', beginAtZero:true, title:{ display:true, text:'ARS'}}, y1:{ type:'linear', position:'right', beginAtZero:true, grid:{ drawOnChartArea:false }, title:{ display:true, text:'Pedidos'}}}} } as ChartConfiguration); this.charts.push(c);
  }
  private crearMetodoPagoChart(): void {
    const ctx=this.metodoPagoChartRef?.nativeElement.getContext('2d'); if (!ctx||!this.kpis()) return;
    const k=this.kpis()!; const entries=Object.entries(k.ingresosPorMetodoPago||{}); if (!entries.length) return;
    const labels=entries.map(([m])=>this.etiquetaMetodo(m)); const values=entries.map(([,v])=>Number(v));
    const colors=['#10b981','#3b82f6','#8b5cf6','#f59e0b','#ef4444'];
    const c=new Chart(ctx,{ type:'doughnut', data:{ labels, datasets:[{ data:values, backgroundColor:colors.slice(0,labels.length), borderWidth:2 }]}, options:{ responsive:true, maintainAspectRatio:false, plugins:{ legend:{ position:'bottom'}}}} as ChartConfiguration); this.charts.push(c);
  }
  private crearStockCatChart(): void {
    const ctx=this.stockCatChartRef?.nativeElement.getContext('2d'); if (!ctx||!this.kpis()) return;
    const k=this.kpis()!; const entries=Object.entries(k.stockPorCategoria||{}); if (!entries.length) return;
    const labels=entries.map(([c])=>c); const values=entries.map(([,v])=>v as number);
    const c=new Chart(ctx,{ type:'bar', data:{ labels, datasets:[{ label:'Stock', data:values, backgroundColor:'#10b981', borderRadius:6 }]}, options:{ indexAxis:'y' as any, responsive:true, maintainAspectRatio:false, plugins:{ legend:{ display:false }}, scales:{ x:{ beginAtZero:true }}}} as ChartConfiguration); this.charts.push(c);
  }
  private crearTopProdChart(): void {
    const ctx=this.topProdChartRef?.nativeElement.getContext('2d'); if (!ctx||!this.kpis()) return;
    const k=this.kpis()!; const top=k.topProductos||[]; if (!top.length) return;
    const labels=top.map(t=>t.nombre.length>18? t.nombre.slice(0,18)+'…':t.nombre); const values=top.map(t=>t.cantidad);
    const c=new Chart(ctx,{ type:'bar', data:{ labels, datasets:[{ label:'Unidades vendidas', data:values, backgroundColor:'#f59e0b', borderRadius:6 }]}, options:{ indexAxis:'y' as any, responsive:true, maintainAspectRatio:false, plugins:{ legend:{ display:false }}, scales:{ x:{ beginAtZero:true }}}} as ChartConfiguration); this.charts.push(c);
  }
  private crearProveedorChart(): void {
    const ctx=this.proveedorChartRef?.nativeElement.getContext('2d'); if (!ctx||!this.kpis()) return;
    const k=this.kpis()!; const entries=Object.entries(k.productosPorProveedor||{}); if (!entries.length) return;
    const labels=entries.map(([n])=>n.length>14? n.slice(0,14)+'…':n); const values=entries.map(([,v])=>v as number);
    const colors=['#3b82f6','#10b981','#f59e0b','#8b5cf6','#ec4899','#14b8a6'];
    const c=new Chart(ctx,{ type:'pie', data:{ labels, datasets:[{ data:values, backgroundColor:colors.slice(0,labels.length)}]}, options:{ responsive:true, maintainAspectRatio:false, plugins:{ legend:{ position:'bottom'}}}} as ChartConfiguration); this.charts.push(c);
  }
  private crearUsuariosChart(): void {
    const ctx=this.usuariosChartRef?.nativeElement.getContext('2d'); if (!ctx||!this.kpis()) return;
    const k=this.kpis()!; const entries=Object.entries(k.usuariosPorRol||{}); if (!entries.length) return;
    const labels=entries.map(([r])=>r); const values=entries.map(([,v])=>v as number);
    const colors=['#3b82f6','#10b981','#f59e0b'];
    const c=new Chart(ctx,{ type:'doughnut', data:{ labels, datasets:[{ data:values, backgroundColor:colors.slice(0,labels.length)}]}, options:{ responsive:true, maintainAspectRatio:false, plugins:{ legend:{ position:'bottom'}}}} as ChartConfiguration); this.charts.push(c);
  }

  etiquetaEstado(e:string){ const m:Record<string,string>={ pendiente:'Pendiente', en_preparacion:'En preparación', enviado:'Enviado', entregado:'Entregado', cancelado:'Cancelado'}; return m[e]??e; }
  estadoColor(e:string){ const m:Record<string,string>={ pendiente:'#f59e0b', en_preparacion:'#3b82f6', enviado:'#8b5cf6', entregado:'#10b981', cancelado:'#ef4444'}; return m[e]??'#6b7280'; }
  etiquetaMetodo(m:string){ const map:Record<string,string>={ EFECTIVO:'Efectivo', DEBITO:'Débito', TARJETA_CREDITO:'Crédito', TRANSFERENCIA:'Transferencia'}; return map[m]??m; }
  maxBarWidth(c:number){ const max=Math.max(...this.estados.map(e=>e.count),1); return (c/max)*100; }
  fmt(n:number){ return new Intl.NumberFormat('es-AR').format(n); }
  fmtARS(n:number){ return '$ '+ new Intl.NumberFormat('es-AR',{ minimumFractionDigits:2 }).format(n); }
}
