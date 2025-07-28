package com.example.ecommerce.controladores;

import com.example.ecommerce.dto.PedidoCreacionDTO;
import com.example.ecommerce.dto.PedidoRespuestaDTO;
import com.example.ecommerce.modelo.entidades.Usuario;
import com.example.ecommerce.repositorios.PedidoRepository;
import com.example.ecommerce.servicios.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controlador REST para el procesamiento y consulta de pedidos.
 * Los endpoints están protegidos por roles de usuario.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Constructor para la inyección de dependencias.
     * @param pedidoService Servicio de pedidos.
     */
    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Crea un nuevo pedido. Accesible por usuarios con rol USER o ADMIN.
     * Para este ejemplo, se asume que el usuario del DTO debe coincidir con el usuario autenticado.
     * @param pedidoCreacionDTO DTO con los datos del pedido a crear.
     * @return ResponseEntity con el DTO del pedido creado y estado 201 CREATED.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PedidoRespuestaDTO> crearPedido(@Valid @RequestBody PedidoCreacionDTO pedidoCreacionDTO, Authentication authentication) {
        // REFINAMIENTO DE SEGURIDAD: Un usuario no debe poder crear pedidos para otros.
        // Forzamos el ID del usuario autenticado, ignorando lo que venga en el DTO.
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();

        // Un administrador puede crear pedidos para otros usuarios si se mantiene el ID del DTO.
        // Un usuario normal solo puede crear pedidos para sí mismo.
        if (!authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            pedidoCreacionDTO.setUsuarioId(usuarioAutenticado.getId());
        }

        PedidoRespuestaDTO nuevoPedido = pedidoService.crearPedido(pedidoCreacionDTO);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    /**
     * Consulta el historial de pedidos de un usuario específico.
     * Accesible por el propio usuario (USER) o por cualquier ADMIN.
     * @param usuarioId El ID del usuario.
     * @return ResponseEntity con una lista de DTOs de pedidos y estado 200 OK.
     */
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or (#usuarioId == authentication.principal.id)")
    public ResponseEntity<Page<PedidoRespuestaDTO>> consultarPedidosDeUsuario(@PathVariable Long usuarioId, Pageable pageable) { // <<--- AÑADIR PAGEABLE
        Page<PedidoRespuestaDTO> pedidos = pedidoService.consultarPedidosDeUsuario(usuarioId, pageable); // <<--- PASAR PAGEABLE
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }


    /**
     * Consulta un pedido específico por su ID.
     * Accesible por el propio usuario (USER) si es su pedido, o por cualquier ADMIN.
     * @param id ID del pedido.
     * @return ResponseEntity con el DTO del pedido y estado 200 OK.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @pedidoSecurity.esPropietarioDePedido(#id, authentication.principal.id)")
    public ResponseEntity<PedidoRespuestaDTO> obtenerPedidoPorId(@PathVariable Long id) {
        PedidoRespuestaDTO pedido = pedidoService.obtenerPedidoPorId(id);
        return new ResponseEntity<>(pedido, HttpStatus.OK);
    }

    /**
     * Consulta todos los pedidos en el sistema. Solo accesible por usuarios con rol ADMIN.
     * @return ResponseEntity con una lista de DTOs de pedidos y estado 200 OK.
     */

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PedidoRespuestaDTO>> consultarTodosLosPedidos(Pageable pageable) { // <<--- AÑADIR PAGEABLE
        Page<PedidoRespuestaDTO> pedidos = pedidoService.consultarTodosLosPedidos(pageable); // <<--- PASAR PAGEABLE
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    // Helper bean para seguridad a nivel de método, para verificar si un usuario es propietario de un pedido
    @Component("pedidoSecurity") // Nombre del bean para usar en @PreAuthorize
    public static class PedidoSecurity {

        @Autowired
        private PedidoRepository pedidoRepository;

        public boolean esPropietarioDePedido(Long pedidoId, Long usuarioId) {
            return pedidoRepository.findById(pedidoId)
                    .map(pedido -> pedido.getUsuario().getId().equals(usuarioId))
                    .orElse(false);
        }
    }
}

