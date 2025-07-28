package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para la petición de autenticación (login).
 */
@Data
public class AutenticacionPeticionDTO {
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String nombreUsuario;
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String contrasena;
}
