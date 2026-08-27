package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "Producto")
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

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getPrecio() { return precio; }
    public void setPrecio(String precio) { this.precio = precio; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public Receta getReceta() { return receta; }
    public void setReceta(Receta receta) { this.receta = receta; }

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