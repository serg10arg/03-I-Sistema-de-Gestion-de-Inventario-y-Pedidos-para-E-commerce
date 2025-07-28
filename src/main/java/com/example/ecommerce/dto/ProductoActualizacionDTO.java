package com.example.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para actualizar un producto. Los campos pueden ser nulos si no se desean actualizar.
 */
@Data
public class ProductoActualizacionDTO {
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    private String nombre;
    private String descripcion;
    @DecimalMin(value = "0.01", message = "El precio debe ser positivo")
    private BigDecimal precio;
    @Min(value = 0, message = "La cantidad en stock no puede ser negativa")
    private Integer cantidadEnStock;
}
