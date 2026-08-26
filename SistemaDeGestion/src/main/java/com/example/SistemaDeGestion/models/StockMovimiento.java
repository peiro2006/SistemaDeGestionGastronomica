package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "Stock_Movimiento")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = Instant.now();
        }
    }

}
