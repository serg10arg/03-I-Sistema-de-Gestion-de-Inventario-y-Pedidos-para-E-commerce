package com.example.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO para la respuesta de un detalle de pedido, incluyendo información del producto.
 */
@Data
public class DetallePedidoRespuestaDTO {
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
