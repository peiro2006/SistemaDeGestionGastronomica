package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Receta")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta")
    private Long idReceta;

    @NotBlank(message = "Debe ingresar un nombre para la receta")
    @Size(min = 2, max = 100, message = "El nombre de la receta debe tener entre 2 a 100 caracteres")
    @Column(name = "nombre_receta")
    private String nombreReceta;

    @NotBlank(message = "Debe ingresar una descripcion para la receta")
    @Column(name = "descripcion_receta")
    private String descripcionReceta;

    @NotBlank(message = "Debe ingresar los ingredientes de la receta")
    @Column(name = "ingredientes_receta")
    private String ingredientesReceta;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecetaInsumo> ingredientes = new ArrayList<>();

    public Long getIdReceta() { return idReceta; }
    public void setIdReceta(Long idReceta) { this.idReceta = idReceta; }
    public String getNombreReceta() { return nombreReceta; }
    public void setNombreReceta(String nombreReceta) { this.nombreReceta = nombreReceta; }
    public String getDescripcionReceta() { return descripcionReceta; }
    public void setDescripcionReceta(String descripcionReceta) { this.descripcionReceta = descripcionReceta; }
    public String getIngredientesReceta() { return ingredientesReceta; }
    public void setIngredientesReceta(String ingredientesReceta) { this.ingredientesReceta = ingredientesReceta; }
    public List<RecetaInsumo> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<RecetaInsumo> ingredientes) { this.ingredientes = ingredientes; }
}