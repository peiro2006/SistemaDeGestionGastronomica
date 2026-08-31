package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "caja")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caja")
    private Long idCaja;

    @Column(name = "nombre_caja", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "moneda", length = 3, nullable = false)
    private String moneda = "ARS";

    @Column(name = "monto_inicial", precision = 12, scale = 2)
    private BigDecimal montoInicial;

    @Column(name = "monto_actual", precision = 12, scale = 2)
    private BigDecimal montoActual;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoCaja estado = EstadoCaja.INACTIVA;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private Instant fechaActualizacion;

    @Column(name = "abierta_por")
    private Long abiertaPor;

    @Column(name = "fecha_apertura")
    private Instant fechaApertura;

    @PrePersist
    public void prePersist() {
        fechaCreacion = Instant.now();
        if (montoActual == null) {
            montoActual = montoInicial;
        }
        if (estado == null) {
            estado = EstadoCaja.INACTIVA;
        }
        // Si monto es 0, no disponible
        if (montoInicial != null && montoInicial.compareTo(BigDecimal.ZERO) == 0) {
            estado = EstadoCaja.NO_DISPONIBLE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = Instant.now();
        // Auto: si monto actual es 0, no disponible
        if (montoActual != null && montoActual.compareTo(BigDecimal.ZERO) == 0) {
            estado = EstadoCaja.NO_DISPONIBLE;
        }
    }

    public boolean estaDisponible() {
        return estado == EstadoCaja.ACTIVA || estado == EstadoCaja.INACTIVA;
    }

    public boolean tieneFondos() {
        return montoActual != null && montoActual.compareTo(BigDecimal.ZERO) > 0;
    }
}