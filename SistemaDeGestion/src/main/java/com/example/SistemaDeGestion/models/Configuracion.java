package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "La clave es obligatoria")
    @Column(name = "clave", nullable = false, unique = true)
    private String clave;

    @NotNull(message = "El valor es obligatorio")
    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
