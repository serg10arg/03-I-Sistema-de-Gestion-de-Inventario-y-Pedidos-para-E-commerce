package com.example.ecommerce.repositorios;

import com.example.ecommerce.modelo.entidades.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Producto, permitiendo operaciones CRUD.
 * Extiende JpaRepository para aprovechar las funcionalidades de Spring Data JPA.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}

