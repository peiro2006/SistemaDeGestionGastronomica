package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "Producto")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @NotBlank(message = "Debe ingresar un nombre para el producto")
    @Size(min = 2, max = 100, message = "El producto debe tener entre 2 a 100 caracteres")
    @Column(name = "nombre_producto")
    private String nombreProducto;

    @NotBlank(message = "Debe ingresar una descripcion para el producto")
    @Column(name = "descripcion_producto")
    private String descripcion;

    @NotBlank(message = "Debe ingresar un precio para el producto")
    @Column(name = "precio_producto")
    private String precio;

    @Column(name = "categoria_producto")
    private String categoria;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "activo")
    private Boolean activo;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Column(name = "stock_actual")
    private Integer stockActual;

    @PositiveOrZero(message = "El stock minimo no puede ser negativo")
    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receta", nullable = false)
    private Receta receta;

    @PrePersist
    protected void onCreate() {
        if (activo == null) {
            activo = true;
        }
        if (stockActual == null) {
            stockActual = 0;
        }
        if (stockMinimo == null) {
            stockMinimo = 0;
        }
    }

}
