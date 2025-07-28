package com.example.ecommerce.controladores;

import com.example.ecommerce.dto.ProductoActualizacionDTO;
import com.example.ecommerce.dto.ProductoCreacionDTO;
import com.example.ecommerce.dto.ProductoRespuestaDTO;
import com.example.ecommerce.servicios.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * Controlador REST para la gestión de productos.
 * Los endpoints están protegidos por roles de usuario.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Constructor para la inyección de dependencias.
     * @param productoService Servicio de productos.
     */
    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Crea un nuevo producto. Solo accesible por usuarios con rol ADMIN.
     * @param productoCreacionDTO DTO con los datos del nuevo producto.
     * @return ResponseEntity con el DTO del producto creado y estado 201 CREATED.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Protegido para el rol ADMIN
    public ResponseEntity<ProductoRespuestaDTO> crearProducto(@Valid @RequestBody ProductoCreacionDTO productoCreacionDTO) {
        ProductoRespuestaDTO nuevoProducto = productoService.crearProducto(productoCreacionDTO);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    /**
     * Obtiene un producto por su ID. Accesible por usuarios con rol ADMIN o USER.
     * @param id ID del producto.
     * @return ResponseEntity con el DTO del producto encontrado y estado 200 OK.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Accesible por ADMIN o USER
    public ResponseEntity<ProductoRespuestaDTO> obtenerProductoPorId(@PathVariable Long id) {
        ProductoRespuestaDTO producto = productoService.obtenerProductoPorId(id);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    /**
     * Obtiene todos los productos de forma paginada. Accesible por usuarios con rol ADMIN o USER.
     * @param pageable Objeto con la información de paginación y ordenamiento.
     * @return ResponseEntity con una página de DTOs de productos y estado 200 OK.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<ProductoRespuestaDTO>> obtenerTodosLosProductos(Pageable pageable) { // <<--- AÑADIR PAGEABLE
        Page<ProductoRespuestaDTO> productos = productoService.obtenerTodosLosProductos(pageable); // <<--- PASAR PAGEABLE
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    /**
     * Actualiza un producto existente. Solo accesible por usuarios con rol ADMIN.
     * @param id ID del producto a actualizar.
     * @param productoActualizacionDTO DTO con los datos para actualizar el producto.
     * @return ResponseEntity con el DTO del producto actualizado y estado 200 OK.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Protegido para el rol ADMIN
    public ResponseEntity<ProductoRespuestaDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoActualizacionDTO productoActualizacionDTO) {
        ProductoRespuestaDTO productoActualizado = productoService.actualizarProducto(id, productoActualizacionDTO);
        return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
    }

    /**
     * Elimina un producto por su ID. Solo accesible por usuarios con rol ADMIN.
     * @param id ID del producto a eliminar.
     * @return ResponseEntity con estado 204 NO CONTENT.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Protegido para el rol ADMIN
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

