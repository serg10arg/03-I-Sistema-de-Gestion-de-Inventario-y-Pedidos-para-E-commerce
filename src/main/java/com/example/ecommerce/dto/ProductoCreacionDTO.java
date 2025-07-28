package com.example.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para la creación de un nuevo producto.
 */
@Data
public class ProductoCreacionDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
    private String descripcion;
    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.01", message = "El precio debe ser positivo")
    private BigDecimal precio;
    @NotNull(message = "La cantidad en stock no puede ser nula")
    @Min(value = 0, message = "La cantidad en stock no puede ser negativa")
    private Integer cantidadEnStock;
}
