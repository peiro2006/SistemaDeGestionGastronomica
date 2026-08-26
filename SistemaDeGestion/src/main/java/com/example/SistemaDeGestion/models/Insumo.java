package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "Insumo")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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

    @PrePersist
    protected void onCreate() {
        if (stockActual == null) {
            stockActual = 0;
        }
    }

}
