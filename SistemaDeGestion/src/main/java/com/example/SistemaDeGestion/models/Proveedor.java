package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "proveedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    @Column(name = "nombre", length = 150)
    private String nombre;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(name = "fecha_creacion")
    @Builder.Default
    private Instant fechaCreacion = Instant.now();

    // Columnas legacy para compatibilidad con DB vieja (se mantienen para no romper NOT NULL)
    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "razon_social", length = 150)
    private String razonSocial;

    @Column(name = "cuit_rut", length = 30)
    private String cuitRut;

    @Column(name = "fecha_alta")
    private Instant fechaAlta;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = Instant.now();
        }
        if (nombre == null) {
            nombre = "Proveedor " + (idProveedor != null ? idProveedor : "");
        }
        // Backfill columnas legacy para satisfacer NOT NULL viejos + evitar duplicado unique
        if (correo == null) correo = email != null && !email.isBlank() ? email : "prov_" + System.nanoTime() + "@tmp.local";
        if (razonSocial == null) razonSocial = nombre != null ? nombre : "";
        if (cuitRut == null) cuitRut = String.valueOf(System.nanoTime()) + (int)(Math.random()*1000);
        if (fechaAlta == null) fechaAlta = fechaCreacion != null ? fechaCreacion : Instant.now();
        if (ciudad == null) ciudad = "Sin ciudad";
        if (telefono == null) telefono = "";
        if (email == null) email = correo != null ? correo : "";
        if (direccion == null) direccion = "";
    }

    public Long getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Long idProveedor) { this.idProveedor = idProveedor; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Instant fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
