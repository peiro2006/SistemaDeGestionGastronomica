package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "Proveedor", uniqueConstraints = @UniqueConstraint(columnNames = "cuit_rut"))
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    @NotBlank(message = "Debe ingresar la razon social o nombre del proveedor")
    @Size(min = 2, max = 100, message = "La razon social debe tener entre 2 a 100 caracteres")
    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @NotBlank(message = "Debe ingresar el CUIT/RUT del proveedor")
    @Size(min = 6, max = 15, message = "El CUIT/RUT debe tener entre 6 a 15 caracteres")
    @Pattern(regexp = "^[0-9]+$", message = "El CUIT/RUT debe contener solo numeros")
    @Column(name = "cuit_rut", nullable = false)
    private String cuitRut;

    @NotBlank(message = "Debe ingresar el telefono del proveedor")
    @Size(min = 6, max = 20, message = "El telefono debe tener entre 6 a 20 caracteres")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "El telefono contiene caracteres no validos")
    @Column(name = "telefono", nullable = false)
    private String telefono;

    @NotBlank(message = "Debe ingresar el correo de contacto del proveedor")
    @Email(message = "Debe ingresar un correo electronico valido")
    @Column(name = "correo", nullable = false)
    private String correo;

    @NotBlank(message = "Debe ingresar la direccion del proveedor")
    @Size(min = 2, max = 200, message = "La direccion debe tener entre 2 a 200 caracteres")
    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;

    @Column(name = "usuario_alta")
    private String usuarioAlta;

    @Column(name = "usuario_ultima_modificacion")
    private String usuarioUltimaModificacion;

    public Long getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Long idProveedor) { this.idProveedor = idProveedor; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getCuitRut() { return cuitRut; }
    public void setCuitRut(String cuitRut) { this.cuitRut = cuitRut; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaUltimaModificacion() { return fechaUltimaModificacion; }
    public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) { this.fechaUltimaModificacion = fechaUltimaModificacion; }
    public String getUsuarioAlta() { return usuarioAlta; }
    public void setUsuarioAlta(String usuarioAlta) { this.usuarioAlta = usuarioAlta; }
    public String getUsuarioUltimaModificacion() { return usuarioUltimaModificacion; }
    public void setUsuarioUltimaModificacion(String usuarioUltimaModificacion) { this.usuarioUltimaModificacion = usuarioUltimaModificacion; }

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}