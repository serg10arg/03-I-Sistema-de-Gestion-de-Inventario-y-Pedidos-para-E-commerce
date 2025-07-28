package com.example.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * DTO para la creación de un nuevo pedido.
 */
@Data
public class PedidoCreacionDTO {
    @NotNull(message = "El ID de usuario no puede ser nulo")
    private Long usuarioId;
    @NotEmpty(message = "El pedido debe contener al menos un detalle")
    @Valid // Valida cada elemento en la lista
    private List<DetallePedidoCreacionDTO> detalles;
}
