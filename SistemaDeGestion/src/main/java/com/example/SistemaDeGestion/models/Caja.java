package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Caja", uniqueConstraints = @UniqueConstraint(columnNames = "nombre_caja"))
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caja")
    private Long idCaja;

    @NotBlank(message = "Debe ingresar un nombre o identificador para la caja")
    @Size(min = 2, max = 100, message = "El nombre de la caja debe tener entre 2 a 100 caracteres")
    @Column(name = "nombre_caja", nullable = false)
    private String nombreCaja;

    @NotNull(message = "Debe ingresar el monto inicial de la caja")
    @DecimalMin(value = "0.0", message = "El monto inicial no puede ser negativo")
    @Column(name = "monto_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoInicial;

    @NotBlank(message = "Debe ingresar la moneda de la caja")
    @Column(name = "moneda", nullable = false)
    private String moneda;

    @Column(name = "descripcion_caja")
    private String descripcionCaja;

    @Column(name = "activa")
    private Boolean activa;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public Long getIdCaja() { return idCaja; }
    public void setIdCaja(Long idCaja) { this.idCaja = idCaja; }
    public String getNombreCaja() { return nombreCaja; }
    public void setNombreCaja(String nombreCaja) { this.nombreCaja = nombreCaja; }
    public BigDecimal getMontoInicial() { return montoInicial; }
    public void setMontoInicial(BigDecimal montoInicial) { this.montoInicial = montoInicial; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getDescripcionCaja() { return descripcionCaja; }
    public void setDescripcionCaja(String descripcionCaja) { this.descripcionCaja = descripcionCaja; }
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @PrePersist
    protected void onCreate() {
        if (activa == null) {
            activa = true;
        }
        if (montoInicial == null) {
            montoInicial = BigDecimal.ZERO;
        }
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}