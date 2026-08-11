package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Size(min = 12, max = 24, message = "El producto debe tener entre 12 a 24 caracteres")
    @Column(name = "nombre_producto")
    private String nombreProducto;

    @NotBlank(message = "Debe ingresar una descripcion para el producto")
    @Column(name = "descripcion_producto")
    private String descripcion;

    @NotNull(message = "Debe ingresar un precio para el producto")
    @Column(name = "precio_producto")
    private String precio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receta", nullable = false)
    private Receta receta;

}
