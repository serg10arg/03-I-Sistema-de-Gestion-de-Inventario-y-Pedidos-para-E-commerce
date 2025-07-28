package com.example.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la respuesta de un pedido, mostrando la información relevante.
 */
@Data
public class PedidoRespuestaDTO {
    private Long id;
    private LocalDateTime fechaCreacion;
    private BigDecimal total;
    private String nombreUsuario; // Nombre de usuario del cliente que realizó el pedido
    private List<DetallePedidoRespuestaDTO> detalles; // Lista de detalles del pedido
}
