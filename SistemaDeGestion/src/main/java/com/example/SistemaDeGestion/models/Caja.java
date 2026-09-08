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
    @Builder.Default
    private String moneda = "ARS";

    @Column(name = "monto_inicial", precision = 12, scale = 2)
    private BigDecimal montoInicial;

    @Column(name = "monto_actual", precision = 12, scale = 2)
    private BigDecimal montoActual;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    @Builder.Default
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
    }

    public boolean estaDisponible() {
        return estado == EstadoCaja.ACTIVA || estado == EstadoCaja.INACTIVA;
    }

    public boolean tieneFondos() {
        return montoActual != null && montoActual.compareTo(BigDecimal.ZERO) > 0;
    }

    public Long getIdCaja() { return idCaja; }
    public void setIdCaja(Long idCaja) { this.idCaja = idCaja; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public BigDecimal getMontoInicial() { return montoInicial; }
    public void setMontoInicial(BigDecimal montoInicial) { this.montoInicial = montoInicial; }
    public BigDecimal getMontoActual() { return montoActual; }
    public void setMontoActual(BigDecimal montoActual) { this.montoActual = montoActual; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public EstadoCaja getEstado() { return estado; }
    public void setEstado(EstadoCaja estado) { this.estado = estado; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Instant fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public Instant getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Instant fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Long getAbiertaPor() { return abiertaPor; }
    public void setAbiertaPor(Long abiertaPor) { this.abiertaPor = abiertaPor; }
    public Instant getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(Instant fechaApertura) { this.fechaApertura = fechaApertura; }
}