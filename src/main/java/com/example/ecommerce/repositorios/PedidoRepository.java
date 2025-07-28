package com.example.ecommerce.repositorios;

import com.example.ecommerce.modelo.entidades.Pedido;
import com.example.ecommerce.modelo.entidades.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio para la entidad Pedido, permitiendo operaciones CRUD.
 */
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Método para encontrar pedidos por usuario
    Page<Pedido> findByUsuario(Usuario usuario, Pageable pageable);

    Page<Pedido> findByUsuarioId(Long usuarioId, Pageable pageable);
}
