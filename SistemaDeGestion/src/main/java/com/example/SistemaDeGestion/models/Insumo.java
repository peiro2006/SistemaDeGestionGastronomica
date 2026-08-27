package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "Insumo")
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumo")
    private Long idInsumo;

    @NotBlank(message = "Debe ingresar un nombre para el insumo")
    @Column(name = "nombre_insumo", unique = true, nullable = false)
    private String nombreInsumo;

    @NotBlank(message = "Debe ingresar la unidad de medida")
    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;

    @NotNull(message = "Debe ingresar el stock del insumo")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    public Long getIdInsumo() { return idInsumo; }
    public void setIdInsumo(Long idInsumo) { this.idInsumo = idInsumo; }
    public String getNombreInsumo() { return nombreInsumo; }
    public void setNombreInsumo(String nombreInsumo) { this.nombreInsumo = nombreInsumo; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

    @PrePersist
    protected void onCreate() {
        if (stockActual == null) {
            stockActual = 0;
        }
    }
}