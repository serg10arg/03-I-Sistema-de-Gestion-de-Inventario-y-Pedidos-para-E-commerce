package com.example.ecommerce.modelo.entidades;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa un pedido de un cliente.
 */
@Entity
@Table(name = "pedidos")
@Data // Genera getters, setters, toString, equals y hashCode con Lombok
@NoArgsConstructor // Genera constructor sin argumentos con Lombok
@AllArgsConstructor // Genera constructor con todos los argumentos con Lombok
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY) // Relación Muchos a Uno con Usuario
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true) // Relación Uno a Muchos con DetallePedido
    private List<DetallePedido> detalles;
}

