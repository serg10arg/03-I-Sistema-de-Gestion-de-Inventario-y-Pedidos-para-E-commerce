package com.example.ecommerce.servicios;

import com.example.ecommerce.dto.ProductoActualizacionDTO;
import com.example.ecommerce.dto.ProductoCreacionDTO;
import com.example.ecommerce.dto.ProductoRespuestaDTO;
import com.example.ecommerce.excepciones.RecursoNoEncontradoException;
import com.example.ecommerce.mapper.ProductoMapper;
import com.example.ecommerce.modelo.entidades.Producto;
import com.example.ecommerce.repositorios.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de productos, conteniendo la lógica de negocio.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    /**
     * Constructor para la inyección de dependencias.
     * @param productoRepository Repositorio de productos.
     * @param productoMapper Mapeador de productos.
     */
    @Autowired
    public ProductoService(ProductoRepository productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    /**
     * Crea un nuevo producto en la base de datos.
     * @param dto Datos para la creación del producto.
     * @return El DTO del producto creado.
     */
    @Transactional // Asegura que la operación sea transaccional
    public ProductoRespuestaDTO crearProducto(ProductoCreacionDTO dto) {
        Producto producto = productoMapper.toEntity(dto);
        Producto productoGuardado = productoRepository.save(producto);
        return productoMapper.toDto(productoGuardado);
    }

    /**
     * Obtiene un producto por su ID.
     * @param id ID del producto.
     * @return El DTO del producto encontrado.
     * @throws RecursoNoEncontradoException Si el producto no es encontrado.
     */
    @Transactional(readOnly = true) // Solo lectura, no requiere bloqueo
    public ProductoRespuestaDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "ID", id));
        return productoMapper.toDto(producto);
    }

    /**
     * Obtiene todos los productos de forma paginada.
     * @param pageable Objeto que contiene la información de paginación y ordenamiento.
     * @return Una página de DTOs de productos.
     */
    @Transactional(readOnly = true)
    public Page<ProductoRespuestaDTO> obtenerTodosLosProductos(Pageable pageable) {
        // El repositorio ya devuelve una Page, solo necesitamos mapear su contenido.
        return productoRepository.findAll(pageable)
                .map(productoMapper::toDto);
    }

    /**
     * Actualiza un producto existente.
     * @param id ID del producto a actualizar.
     * @param dto Datos para la actualización del producto.
     * @return El DTO del producto actualizado.
     * @throws RecursoNoEncontradoException Si el producto no es encontrado.
     */
    @Transactional
    public ProductoRespuestaDTO actualizarProducto(Long id, ProductoActualizacionDTO dto) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "ID", id));

        // Aplica solo los campos no nulos del DTO a la entidad existente
        productoMapper.updateEntityFromDto(dto, productoExistente);

        Producto productoActualizado = productoRepository.save(productoExistente);
        return productoMapper.toDto(productoActualizado);
    }

    /**
     * Elimina un producto por su ID.
     * @param id ID del producto a eliminar.
     * @throws RecursoNoEncontradoException Si el producto no es encontrado.
     */
    @Transactional
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto", "ID", id);
        }
        productoRepository.deleteById(id);
    }
}
