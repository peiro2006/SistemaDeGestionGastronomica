package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioCreateReqDto (

        @NotBlank(message = "Debe ingresar su nombre")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String nombre,

        @NotBlank(message = "Debe ingresar su apellido")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
        String apellido,

        @NotBlank(message = "Debe ingresar su email")
        @Email(message = "Debe ingresar un email valido")
        String email,

        @NotBlank(message = "Debe ingresar una contrasena")
        @Size(min = 8, max = 32, message = "La contrasena debe tener entre 8 y 32 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "La contrasena debe contener al menos una mayuscula, una minuscula, un numero y un caracter especial"
        )
        String password

) {
}
