package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "Receta")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta")
    private Long idReceta;

    @NotBlank(message = "Debe ingresar un nombre para la receta")
    @Size(min = 12, max = 24, message = "El nombre de la receta debe tener entre 12 a 24 caracteres")
    @Column(name = "nombre_receta")
    private String nombreReceta;

    @NotBlank(message = "Debe ingresar una descripcion para la receta")
    @Column(name = "descripcion_receta")
    private String descripcionReceta;

    @NotBlank(message = "Debe ingresar los ingredientes de la receta")
    @Column(name = "ingredientes_receta")
    private String ingredientesReceta;

}
