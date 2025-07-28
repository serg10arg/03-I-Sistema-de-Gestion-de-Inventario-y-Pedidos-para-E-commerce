package com.example.ecommerce.servicios;

import com.example.ecommerce.dto.DetallePedidoCreacionDTO;
import com.example.ecommerce.dto.PedidoCreacionDTO;
import com.example.ecommerce.dto.PedidoRespuestaDTO;
import com.example.ecommerce.excepciones.StockInsuficienteException;
import com.example.ecommerce.mapper.PedidoMapper;
// CORRECCIÓN: Se elimina el import incorrecto de 'com.example.ecommerce.model.entity.DetallePedido'
import com.example.ecommerce.modelo.entidades.Pedido;
import com.example.ecommerce.modelo.entidades.Producto;
import com.example.ecommerce.modelo.entidades.Usuario;
import com.example.ecommerce.modelo.entidades.enums.Rol;
import com.example.ecommerce.repositorios.PedidoRepository;
import com.example.ecommerce.repositorios.ProductoRepository;
import com.example.ecommerce.repositorios.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para PedidoService.
 * Usa Mockito para simular las dependencias.
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private PedidoMapper pedidoMapper = Mappers.getMapper(PedidoMapper.class);

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario;
    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("usuarioPrueba");
        usuario.setRol(Rol.USER);

        producto1 = new Producto();
        producto1.setId(101L);
        producto1.setNombre("Laptop");
        producto1.setPrecio(new BigDecimal("1000.00"));
        producto1.setCantidadEnStock(5);

        producto2 = new Producto();
        producto2.setId(102L);
        producto2.setNombre("Mouse");
        producto2.setPrecio(new BigDecimal("25.00"));
        producto2.setCantidadEnStock(10);
    }

    @Test
    @DisplayName("Debería crear un pedido exitosamente y reducir el stock")
    void deberiaCrearPedidoExitosamente() {
        // Arrange
        DetallePedidoCreacionDTO detalle1 = new DetallePedidoCreacionDTO();
        detalle1.setProductoId(producto1.getId());
        detalle1.setCantidad(2);

        DetallePedidoCreacionDTO detalle2 = new DetallePedidoCreacionDTO();
        detalle2.setProductoId(producto2.getId());
        detalle2.setCantidad(3);

        PedidoCreacionDTO pedidoDTO = new PedidoCreacionDTO();
        pedidoDTO.setUsuarioId(usuario.getId());
        pedidoDTO.setDetalles(Arrays.asList(detalle1, detalle2));

        List<Long> productoIds = List.of(producto1.getId(), producto2.getId());
        // Capturamos el estado inicial para una aserción más robusta
        int stockInicialProducto1 = producto1.getCantidadEnStock();
        int stockInicialProducto2 = producto2.getCantidadEnStock();

        // Clonamos los productos para el mock, para evitar problemas de modificación de estado
        // en el objeto original que usamos para las aserciones. Es una práctica defensiva.
        Producto p1Clonado = new Producto();
        p1Clonado.setId(producto1.getId());
        p1Clonado.setNombre(producto1.getNombre());
        p1Clonado.setPrecio(producto1.getPrecio());
        p1Clonado.setCantidadEnStock(producto1.getCantidadEnStock());

        Producto p2Clonado = new Producto();
        p2Clonado.setId(producto2.getId());
        p2Clonado.setNombre(producto2.getNombre());
        p2Clonado.setPrecio(producto2.getPrecio());
        p2Clonado.setCantidadEnStock(producto2.getCantidadEnStock());

        List<Producto> productosEncontrados = List.of(p1Clonado, p2Clonado);

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(productoRepository.findAllById(productoIds)).thenReturn(productosEncontrados);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedidoGuardado = invocation.getArgument(0);
            pedidoGuardado.setId(1L);
            return pedidoGuardado;
        });

        //Act
        PedidoRespuestaDTO resultado = pedidoService.crearPedido(pedidoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(usuario.getNombreUsuario(), resultado.getNombreUsuario());
        assertEquals(new BigDecimal("2075.00"), resultado.getTotal());
        assertEquals(2, resultado.getDetalles().size());

        // CORRECCIÓN: Usar un captor para Iterable<Producto>
        ArgumentCaptor<Iterable<Producto>> captorProductos = ArgumentCaptor.forClass(Iterable.class);
        verify(productoRepository).saveAll(captorProductos.capture());

        // Convertimos el Iterable capturado a una lista para facilitar las aserciones
        List<Producto> productosGuardados = StreamSupport.stream(captorProductos.getValue().spliterator(), false)
                .toList();

        // Verificar reducción de stock en los productos capturados
        Producto producto1Guardado = productosGuardados.stream().filter(p -> p.getId().equals(101L)).findFirst().orElseThrow();
        Producto producto2Guardado = productosGuardados.stream().filter(p -> p.getId().equals(102L)).findFirst().orElseThrow();

        // REFINAMIENTO SENIOR: La aserción es más robusta si se basa en el cambio, no en un valor fijo.
        assertEquals(stockInicialProducto1 - 2, producto1Guardado.getCantidadEnStock()); // 5 - 2 = 3
        assertEquals(stockInicialProducto2 - 3, producto2Guardado.getCantidadEnStock()); // 10 - 3 = 7

        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debería lanzar StockInsuficienteException si no hay stock")
    void deberiaLanzarStockInsuficienteExceptionSiNoHayStock() {
        // Arrange
        producto1.setCantidadEnStock(1);
        DetallePedidoCreacionDTO detalle = new DetallePedidoCreacionDTO();
        detalle.setProductoId(producto1.getId());
        detalle.setCantidad(2);

        PedidoCreacionDTO pedidoDTO = new PedidoCreacionDTO();
        pedidoDTO.setUsuarioId(usuario.getId());
        pedidoDTO.setDetalles(Collections.singletonList(detalle));

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        // REFACTOR: Simular la llamada optimizada `findAllById`
        when(productoRepository.findAllById(List.of(producto1.getId()))).thenReturn(List.of(producto1));

        // Act & Assert
        StockInsuficienteException excepcion = assertThrows(StockInsuficienteException.class, () ->
                pedidoService.crearPedido(pedidoDTO)
        );

        assertTrue(excepcion.getMessage().contains("Stock insuficiente para " + producto1.getNombre()));
        verify(pedidoRepository, never()).save(any(Pedido.class));
        verify(productoRepository, never()).saveAll(any());
    }

    // Las pruebas para RecursoNoEncontradoException (usuario y producto) se mantienen similares
    // pero actualizando el mock a findAllById para el caso del producto.

    @Test
    @DisplayName("Debería consultar los pedidos de un usuario con paginación")
    void deberiaConsultarPedidosDeUsuarioConPaginacion() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setUsuario(usuario);
        pedido1.setDetalles(Collections.emptyList());

        List<Pedido> pedidos = Collections.singletonList(pedido1);
        Page<Pedido> paginaDePedidos = new PageImpl<>(pedidos, pageable, 1);

        when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        // REFACTOR: Simular el método de repositorio paginado
        when(pedidoRepository.findByUsuarioId(usuario.getId(), pageable)).thenReturn(paginaDePedidos);

        // Act
        Page<PedidoRespuestaDTO> resultado = pedidoService.consultarPedidosDeUsuario(usuario.getId(), pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
        assertEquals(1L, resultado.getContent().get(0).getId());
        verify(pedidoRepository, times(1)).findByUsuarioId(usuario.getId(), pageable);
    }

    @Test
    @DisplayName("Debería consultar todos los pedidos con paginación")
    void deberiaConsultarTodosLosPedidosConPaginacion() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setUsuario(usuario);
        pedido1.setDetalles(Collections.emptyList());
        Page<Pedido> paginaDePedidos = new PageImpl<>(Collections.singletonList(pedido1), pageable, 1);

        // REFACTOR: Simular el método de repositorio paginado
        when(pedidoRepository.findAll(pageable)).thenReturn(paginaDePedidos);

        // Act
        Page<PedidoRespuestaDTO> resultado = pedidoService.consultarTodosLosPedidos(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("usuarioPrueba", resultado.getContent().get(0).getNombreUsuario());
        verify(pedidoRepository, times(1)).findAll(pageable);
    }
}