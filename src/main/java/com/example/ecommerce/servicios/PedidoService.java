package com.example.ecommerce.servicios;

import com.example.ecommerce.dto.DetallePedidoCreacionDTO;
import com.example.ecommerce.dto.DetallePedidoRespuestaDTO;
import com.example.ecommerce.dto.PedidoCreacionDTO;
import com.example.ecommerce.dto.PedidoRespuestaDTO;
import com.example.ecommerce.excepciones.RecursoNoEncontradoException;
import com.example.ecommerce.excepciones.StockInsuficienteException;
import com.example.ecommerce.mapper.PedidoMapper;
import com.example.ecommerce.modelo.entidades.DetallePedido;
import com.example.ecommerce.modelo.entidades.Pedido;
import com.example.ecommerce.modelo.entidades.Producto;
import com.example.ecommerce.modelo.entidades.Usuario;
import com.example.ecommerce.repositorios.PedidoRepository;
import com.example.ecommerce.repositorios.ProductoRepository;
import com.example.ecommerce.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Servicio para el procesamiento y consulta de pedidos.
 * Contiene la lógica de negocio para verificar stock y actualizarlo.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoMapper pedidoMapper;

    /**
     * Constructor para la inyección de dependencias.
     * @param pedidoRepository Repositorio de pedidos.
     * @param productoRepository Repositorio de productos.
     * @param usuarioRepository Repositorio de usuarios.
     * @param pedidoMapper Mapeador de pedidos.
     */
    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoMapper = pedidoMapper;
    }

    /**
     * Crea un nuevo pedido.
     * Esta lógica verifica el stock, actualiza el stock y calcula el total del pedido.
     * @param pedidoCreacionDTO El DTO con los datos del pedido a crear.
     * @return El DTO del pedido creado.
     * @throws RecursoNoEncontradoException Si el usuario o algún producto no es encontrado.
     * @throws StockInsuficienteException Si el stock de algún producto es insuficiente.
     */
    @Transactional
    public PedidoRespuestaDTO crearPedido(PedidoCreacionDTO pedidoCreacionDTO) {
        // 1. Validar usuario
        Usuario usuario = usuarioRepository.findById(pedidoCreacionDTO.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "ID", pedidoCreacionDTO.getUsuarioId()));

        // REFINAMIENTO: Buscar todos los productos en una sola consulta para eficiencia.
        List<Long> productoIds = pedidoCreacionDTO.getDetalles().stream()
                .map(DetallePedidoCreacionDTO::getProductoId)
                .collect(Collectors.toList());
        Map<Long, Producto> productosEncontrados = productoRepository.findAllById(productoIds).stream()
                .collect(Collectors.toMap(Producto::getId, Function.identity()));

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        nuevoPedido.setUsuario(usuario);
        BigDecimal totalPedido = BigDecimal.ZERO;
        List<DetallePedido> detallesDelPedido = new ArrayList<>();

        // 2. Procesar detalles
        for (var detalleDTO : pedidoCreacionDTO.getDetalles()) {
            Producto producto = productosEncontrados.get(detalleDTO.getProductoId());
            if (producto == null) {
                throw new RecursoNoEncontradoException("Producto", "ID", detalleDTO.getProductoId());
            }

            if (producto.getCantidadEnStock() < detalleDTO.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para " + producto.getNombre());
            }

            producto.setCantidadEnStock(producto.getCantidadEnStock() - detalleDTO.getCantidad());

            DetallePedido detallePedido = new DetallePedido();
            detallePedido.setPedido(nuevoPedido);
            detallePedido.setProducto(producto);
            detallePedido.setCantidad(detalleDTO.getCantidad());
            detallePedido.setPrecioUnitario(producto.getPrecio());
            detallesDelPedido.add(detallePedido);
            totalPedido = totalPedido.add(producto.getPrecio().multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));
        }

        // REFINAMIENTO: Guardar todos los productos actualizados en una sola operación de lote.
        productoRepository.saveAll(productosEncontrados.values());

        nuevoPedido.setTotal(totalPedido);
        nuevoPedido.setDetalles(detallesDelPedido);

        // 3. Guardar el pedido y sus detalles (gracias a CascadeType.ALL)
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

        // El mapeo ahora es mucho más simple
        return mapToDto(pedidoGuardado);
    }

    /**
     * Consulta el historial de pedidos de un usuario específico de forma paginada.
     * La seguridad a nivel de método asegura que un usuario solo pueda ver sus propios pedidos,
     * a menos que sea un administrador.
     * @param usuarioId El ID del usuario.
     * @param pageable Objeto que contiene la información de paginación y ordenamiento.
     * @return Una página de DTOs de pedidos del usuario.
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or #usuarioId == authentication.principal.id")
    public Page<PedidoRespuestaDTO> consultarPedidosDeUsuario(Long usuarioId, Pageable pageable) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException("Usuario", "ID", usuarioId);
        }
        return pedidoRepository.findByUsuarioId(usuarioId, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<PedidoRespuestaDTO> consultarTodosLosPedidos(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public PedidoRespuestaDTO obtenerPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", "ID", id));
        return mapToDto(pedido);
    }

    /**
     * REFINAMIENTO: Método de ayuda privado para centralizar el mapeo y evitar duplicación.
     */
    private PedidoRespuestaDTO mapToDto(Pedido pedido) {
        PedidoRespuestaDTO dto = pedidoMapper.toDto(pedido);
        dto.setDetalles(pedidoMapper.toDetallePedidoDtoList(pedido.getDetalles()));
        return dto;
    }
}
