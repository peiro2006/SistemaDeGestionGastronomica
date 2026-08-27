package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

@Entity
@Table(name = "Stock_Movimiento")
public class StockMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock_movimiento")
    private Long idStockMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo")
    private Insumo insumo;

    @NotBlank(message = "Debe ingresar el tipo de movimiento")
    @Column(name = "tipo", nullable = false)
    private String tipo;

    @NotNull(message = "Debe ingresar la cantidad")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotBlank(message = "Debe ingresar un motivo")
    @Column(name = "motivo", nullable = false)
    private String motivo;

    @NotNull(message = "Debe registrarse el saldo posterior")
    @Column(name = "saldo_posterior", nullable = false)
    private Integer saldoPosterior;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha", nullable = false, updatable = false)
    private Instant fecha;

    public Long getIdStockMovimiento() { return idStockMovimiento; }
    public void setIdStockMovimiento(Long idStockMovimiento) { this.idStockMovimiento = idStockMovimiento; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo insumo) { this.insumo = insumo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public Integer getSaldoPosterior() { return saldoPosterior; }
    public void setSaldoPosterior(Integer saldoPosterior) { this.saldoPosterior = saldoPosterior; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Instant getFecha() { return fecha; }
    public void setFecha(Instant fecha) { this.fecha = fecha; }

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = Instant.now();
        }
    }
}