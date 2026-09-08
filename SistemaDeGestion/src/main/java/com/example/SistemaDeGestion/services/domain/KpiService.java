package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.models.PedidoItem;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.PedidoItemRepository;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import com.example.SistemaDeGestion.repositories.ProveedorRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class KpiService {

    private final PedidoRepository pedidoRepository;
    private final ProductosRepository productosRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerKPIs() {
        Map<String, Object> kpis = new HashMap<>();

        // === BASE ===
        List<Pedido> todosPedidos = pedidoRepository.findAll();
        List<Producto> todosProductos = productosRepository.findAll();
        List<PedidoItem> todosItems = pedidoItemRepository.findAll();
        List<Usuario> todosUsuarios = usuarioRepository.findAll();

        Long totalPedidos = (long) todosPedidos.size();
        kpis.put("totalPedidos", totalPedidos);

        BigDecimal ingresosTotales = pedidoRepository.sumTotales();
        if (ingresosTotales == null) ingresosTotales = todosPedidos.stream().map(Pedido::getTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        kpis.put("ingresosTotales", ingresosTotales);

        BigDecimal promedio = pedidoRepository.avgTotal();
        if (promedio == null) promedio = totalPedidos > 0 ? ingresosTotales.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        kpis.put("promedioPedido", promedio);

        Long totalProductos = (long) todosProductos.size();
        kpis.put("totalProductos", totalProductos);
        Long productosActivos = todosProductos.stream().filter(p -> Boolean.TRUE.equals(p.getActivo())).count();
        kpis.put("productosActivos", productosActivos);
        Long productosInactivos = totalProductos - productosActivos;
        kpis.put("productosInactivos", productosInactivos);

        BigDecimal stockTotal = productosRepository.sumStock();
        if (stockTotal == null) stockTotal = BigDecimal.valueOf(todosProductos.stream().mapToLong(p -> p.getStockActual() != null ? p.getStockActual() : 0).sum());
        kpis.put("stockTotal", stockTotal);
        long stockBajo = todosProductos.stream().filter(p -> p.getStockActual() != null && p.getStockMinimo() != null && p.getStockActual() <= p.getStockMinimo()).count();
        kpis.put("stockBajo", stockBajo);

        // === PEDIDOS POR ESTADO ===
        Map<String, Long> pedidosPorEstado = todosPedidos.stream().collect(Collectors.groupingBy(p -> p.getEstado().name(), Collectors.counting()));
        kpis.put("pedidosPorEstado", pedidosPorEstado);
        long entregados = pedidosPorEstado.getOrDefault("entregado", 0L);
        long cancelados = pedidosPorEstado.getOrDefault("cancelado", 0L);
        double tasaExito = totalPedidos > 0 ? (entregados * 100.0 / totalPedidos) : 0;
        double tasaCancelacion = totalPedidos > 0 ? (cancelados * 100.0 / totalPedidos) : 0;
        kpis.put("tasaExito", Math.round(tasaExito * 100.0) / 100.0);
        kpis.put("tasaCancelacion", Math.round(tasaCancelacion * 100.0) / 100.0);

        // === TIEMPO DEFINIDO: 7 / 30 / 90 días ===
        Instant hace7 = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant hace30 = Instant.now().minus(30, ChronoUnit.DAYS);
        Instant hace90 = Instant.now().minus(90, ChronoUnit.DAYS);
        BigDecimal ingresos7 = sumByFecha(todosPedidos, hace7);
        BigDecimal ingresos30 = sumByFecha(todosPedidos, hace30);
        BigDecimal ingresos90 = sumByFecha(todosPedidos, hace90);
        kpis.put("ingresos7Dias", ingresos7);
        kpis.put("ingresos30Dias", ingresos30);
        kpis.put("ingresos90Dias", ingresos90);
        kpis.put("pedidos7Dias", countByFecha(todosPedidos, hace7));
        kpis.put("pedidos30Dias", countByFecha(todosPedidos, hace30));
        kpis.put("pedidos90Dias", countByFecha(todosPedidos, hace90));

        // === TENDENCIA DIARIA últimos 30 días (para gráfico) ===
        kpis.put("tendenciaDiaria", buildTendenciaDiaria(todosPedidos, 30));
        kpis.put("tendenciaSemanal", buildTendenciaDiaria(todosPedidos, 7));

        // === VENTAS: Tasa conversión y CAC ===
        double tasaConversion = totalProductos > 0 ? (totalPedidos.doubleValue() / totalProductos.doubleValue() * 100) : 0;
        kpis.put("tasaConversion", Math.round(tasaConversion * 100.0) / 100.0);
        BigDecimal cac = totalPedidos > 0 ? ingresosTotales.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        kpis.put("cac", cac);
        // CAC 30 días
        long pedidos30 = countByFecha(todosPedidos, hace30);
        BigDecimal cac30 = pedidos30 > 0 ? ingresos30.divide(BigDecimal.valueOf(pedidos30), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        kpis.put("cac30", cac30);

        // === MARKETING: Tráfico y CTR (simulado a partir de pedidos) ===
        // Tráfico web mock = pedidos * factor 4.2 (visitas que no convierten) + base
        long trafico30 = pedidos30 > 0 ? Math.round(pedidos30 * 4.2 + 18) : 0;
        long trafico7 = Math.round(countByFecha(todosPedidos, hace7) * 4.2 + 6);
        double ctr = totalPedidos > 0 && trafico30 > 0 ? (pedidos30 * 100.0 / trafico30) : 0;
        double ctr7 = countByFecha(todosPedidos, hace7) > 0 && trafico7 > 0 ? (countByFecha(todosPedidos, hace7) * 100.0 / trafico7) : 0;
        kpis.put("traficoWeb30", trafico30);
        kpis.put("traficoWeb7", trafico7);
        kpis.put("ctr", Math.round(ctr * 100.0) / 100.0);
        kpis.put("ctr7", Math.round(ctr7 * 100.0) / 100.0);

        // === FINANZAS: ROI, Margen ===
        double margen = productosActivos > 0 ? ingresosTotales.doubleValue() / productosActivos.doubleValue() : 0;
        kpis.put("margenBeneficio", Math.round(margen * 100.0) / 100.0);
        double roi = stockTotal.compareTo(BigDecimal.ZERO) > 0 ? ingresosTotales.doubleValue() / stockTotal.doubleValue() : 0;
        kpis.put("roi", Math.round(roi * 100.0) / 100.0);
        // Margen neto 30d
        double margen30 = pedidos30 > 0 ? ingresos30.doubleValue() / (double) pedidos30 : 0;
        kpis.put("margen30", Math.round(margen30 * 100.0) / 100.0);
        // Ingresos por método de pago
        Map<String, BigDecimal> ingresosPorMetodo = new HashMap<>();
        Map<String, Long> pedidosPorMetodo = new HashMap<>();
        for (Pedido p : todosPedidos) {
            String m = p.getMetDePago() != null ? p.getMetDePago().name() : "EFECTIVO";
            ingresosPorMetodo.merge(m, p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO, BigDecimal::add);
            pedidosPorMetodo.merge(m, 1L, Long::sum);
        }
        kpis.put("ingresosPorMetodoPago", ingresosPorMetodo);
        kpis.put("pedidosPorMetodoPago", pedidosPorMetodo);

        // === STOCK: por categoría y por proveedor ===
        Map<String, Long> stockPorCategoria = todosProductos.stream().collect(Collectors.groupingBy(p -> p.getCategoria() != null ? p.getCategoria() : "Sin categoria", Collectors.summingLong(p -> p.getStockActual() != null ? p.getStockActual() : 0)));
        Map<String, Long> productosPorCategoria = todosProductos.stream().collect(Collectors.groupingBy(p -> p.getCategoria() != null ? p.getCategoria() : "Sin categoria", Collectors.counting()));
        kpis.put("stockPorCategoria", stockPorCategoria);
        kpis.put("productosPorCategoria", productosPorCategoria);

        Map<String, Long> productosPorProveedor = todosProductos.stream().collect(Collectors.groupingBy(p -> p.getProveedor() != null && p.getProveedor().getNombre() != null ? p.getProveedor().getNombre() : "Sin proveedor", Collectors.counting()));
        Map<String, Long> stockPorProveedor = todosProductos.stream().collect(Collectors.groupingBy(p -> p.getProveedor() != null && p.getProveedor().getNombre() != null ? p.getProveedor().getNombre() : "Sin proveedor", Collectors.summingLong(p -> p.getStockActual() != null ? p.getStockActual() : 0)));
        kpis.put("productosPorProveedor", productosPorProveedor);
        kpis.put("stockPorProveedor", stockPorProveedor);
        kpis.put("totalProveedores", (long) proveedorRepository.count());
        kpis.put("proveedoresActivos", proveedorRepository.findByActivoTrueOrderByNombre().size());

        // === TOP PRODUCTOS (por cantidad vendida) ===
        Map<String, Long> cantidadPorProducto = todosItems.stream().collect(Collectors.groupingBy(i -> i.getProducto() != null ? i.getProducto().getNombreProducto() : "Desconocido", Collectors.summingLong(PedidoItem::getCantidad)));
        Map<String, BigDecimal> ingresosPorProducto = todosItems.stream().collect(Collectors.groupingBy(i -> i.getProducto() != null ? i.getProducto().getNombreProducto() : "Desconocido", Collectors.mapping(PedidoItem::getSubtotal, Collectors.reducing(BigDecimal.ZERO, (a,b) -> a.add(b != null ? b : BigDecimal.ZERO)))));
        List<Map<String, Object>> topProductos = cantidadPorProducto.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(5).map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("nombre", e.getKey());
            m.put("cantidad", e.getValue());
            m.put("ingresos", ingresosPorProducto.getOrDefault(e.getKey(), BigDecimal.ZERO));
            return m;
        }).collect(Collectors.toList());
        kpis.put("topProductos", topProductos);

        // === RRHH: retención ===
        Map<String, Long> usuariosPorRol = todosUsuarios.stream().collect(Collectors.groupingBy(u -> u.getRol() != null ? u.getRol() : "USER", Collectors.counting()));
        kpis.put("usuariosPorRol", usuariosPorRol);
        kpis.put("totalUsuarios", (long) todosUsuarios.size());
        long usuariosConPedidos = todosPedidos.stream().map(p -> p.getUsuario().getIdUsuario()).distinct().count();
        double retencion = todosUsuarios.size() > 0 ? (usuariosConPedidos * 100.0 / todosUsuarios.size()) : 0;
        kpis.put("tasaRetencion", Math.round(retencion * 100.0) / 100.0);
        // Rotación mock inversa
        kpis.put("tasaRotacion", Math.round((100 - retencion) * 100.0) / 100.0);

        // === OBJETIVOS ESTRATÉGICOS: cumplimiento metas ===
        BigDecimal metaMensual = new BigDecimal("100000");
        double cumplimiento = metaMensual.compareTo(BigDecimal.ZERO) > 0 ? ingresos30.doubleValue() / metaMensual.doubleValue() * 100 : 0;
        kpis.put("metaMensual", metaMensual);
        kpis.put("cumplimientoMeta", Math.round(cumplimiento * 100.0) / 100.0);

        // === RESUMEN TIEMPO ===
        kpis.put("periodo", "Últimos 30 días");
        kpis.put("timestamp", Instant.now().toString());

        return kpis;
    }

    private BigDecimal sumByFecha(List<Pedido> pedidos, Instant desde) {
        return pedidos.stream().filter(p -> p.getFechaCreacion() != null && p.getFechaCreacion().isAfter(desde)).map(Pedido::getTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private long countByFecha(List<Pedido> pedidos, Instant desde) {
        return pedidos.stream().filter(p -> p.getFechaCreacion() != null && p.getFechaCreacion().isAfter(desde)).count();
    }
    private List<Map<String, Object>> buildTendenciaDiaria(List<Pedido> pedidos, int dias) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate hoy = LocalDate.now(zone);
        List<Map<String, Object>> lista = new ArrayList<>();
        for (int i = dias - 1; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            Instant inicio = fecha.atStartOfDay(zone).toInstant();
            Instant fin = fecha.plusDays(1).atStartOfDay(zone).toInstant();
            BigDecimal ingresos = pedidos.stream().filter(p -> p.getFechaCreacion() != null && !p.getFechaCreacion().isBefore(inicio) && p.getFechaCreacion().isBefore(fin)).map(Pedido::getTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            long count = pedidos.stream().filter(p -> p.getFechaCreacion() != null && !p.getFechaCreacion().isBefore(inicio) && p.getFechaCreacion().isBefore(fin)).count();
            Map<String, Object> m = new HashMap<>();
            m.put("fecha", fecha.toString());
            m.put("label", String.format("%02d/%02d", fecha.getDayOfMonth(), fecha.getMonthValue()));
            m.put("ingresos", ingresos);
            m.put("pedidos", count);
            lista.add(m);
        }
        return lista;
    }
}
