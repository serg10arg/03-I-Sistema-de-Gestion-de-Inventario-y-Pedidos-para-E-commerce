package com.example.ecommerce.modelo.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad que representa un detalle de un pedido (línea de pedido).
 */
@Entity
@Table(name = "detalles_pedido")
@Data // Genera getters, setters, toString, equals y hashCode con Lombok
@NoArgsConstructor // Genera constructor sin argumentos con Lombok
@AllArgsConstructor // Genera constructor con todos los argumentos con Lombok
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Min(value = 1, message = "La cantidad debe ser mayor que cero.")
    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal precioUnitario; // Precio del producto en el momento de la compra
}

