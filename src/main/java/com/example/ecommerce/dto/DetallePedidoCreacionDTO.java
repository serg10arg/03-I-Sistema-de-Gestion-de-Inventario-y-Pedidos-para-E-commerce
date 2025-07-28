package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para representar un detalle de un pedido al momento de su creación.
 */
@Data
public class DetallePedidoCreacionDTO {
    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long productoId;
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
