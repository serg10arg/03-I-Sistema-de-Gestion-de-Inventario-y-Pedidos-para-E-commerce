package com.example.ecommerce.repositorios;

import com.example.ecommerce.modelo.entidades.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad DetallePedido.
 */
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}
