package com.example.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO para la respuesta de un producto, mostrando la información relevante.
 */
@Data
public class ProductoRespuestaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer cantidadEnStock;
}
