package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "movimiento_contable")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoContable {

    public static final String TIPO_INGRESO = "INGRESO";
    public static final String TIPO_EGRESO = "EGRESO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "concepto", length = 255)
    private String concepto;

    @Column(name = "metodo_pago", length = 15)
    private String metodoPago;

    @Column(name = "id_caja")
    private Long idCaja;

    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @Column(name = "registrado_por", length = 100)
    private String registradoPor;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = Instant.now();
        }
    }
}
