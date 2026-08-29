package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProveedorCreateReqDto (

        @NotBlank(message = "Debe ingresar la razon social o nombre del proveedor")
        @Size(min = 2, max = 100, message = "La razon social debe tener entre 2 a 100 caracteres")
        String razonSocial,

        @NotBlank(message = "Debe ingresar el CUIT/RUT del proveedor")
        @Size(min = 6, max = 15, message = "El CUIT/RUT debe tener entre 6 a 15 caracteres")
        @Pattern(regexp = "^[0-9]+$", message = "El CUIT/RUT debe contener solo numeros")
        String cuitRut,

        @NotBlank(message = "Debe ingresar el telefono del proveedor")
        @Size(min = 6, max = 20, message = "El telefono debe tener entre 6 a 20 caracteres")
        @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "El telefono contiene caracteres no validos")
        String telefono,

        @NotBlank(message = "Debe ingresar el correo de contacto del proveedor")
        @Email(message = "Debe ingresar un correo electronico valido")
        String correo,

        @NotBlank(message = "Debe ingresar la direccion del proveedor")
        @Size(min = 2, max = 200, message = "La direccion debe tener entre 2 a 200 caracteres")
        String direccion

) {
}