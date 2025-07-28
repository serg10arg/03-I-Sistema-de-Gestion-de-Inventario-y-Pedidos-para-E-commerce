package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.DetallePedidoCreacionDTO;
import com.example.ecommerce.dto.DetallePedidoRespuestaDTO;
import com.example.ecommerce.dto.PedidoCreacionDTO;
import com.example.ecommerce.dto.PedidoRespuestaDTO;
import com.example.ecommerce.modelo.entidades.DetallePedido;
import com.example.ecommerce.modelo.entidades.Pedido;
import com.example.ecommerce.modelo.entidades.Producto;
import com.example.ecommerce.modelo.entidades.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Interfaz de mapeo para las entidades Pedido y DetallePedido y sus DTOs.
 * Usa MapStruct para generar la implementación durante la compilación.
 */
@Mapper(componentModel = "spring")
public interface PedidoMapper {

    /**
     * Mapea un PedidoCreacionDTO a una entidad Pedido.
     * Se mapean campos directos y se delega el mapeo de detalles a otro método.
     * Se ignora el ID, la fecha de creación y el total ya que serán generados por la lógica de negocio.
     * Se usa un método @Named para mapear el usuario.
     * @param dto El DTO de creación del pedido.
     * @return La entidad Pedido mapeada.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "usuario", source = "usuarioId", qualifiedByName = "mapUsuarioDesdeId")
    @Mapping(target = "detalles", ignore = true) // Los detalles se manejan en el servicio
    Pedido toEntity(PedidoCreacionDTO dto);

    /**
     * Mapea una entidad Pedido a un PedidoRespuestaDTO.
     * Se mapea el nombre de usuario del Pedido.
     * Se delega el mapeo de los detalles a la lista de DetallePedidoRespuestaDTO.
     * @param entity La entidad Pedido.
     * @return El DTO de respuesta del pedido.
     */
    @Mapping(source = "usuario.nombreUsuario", target = "nombreUsuario")
    PedidoRespuestaDTO toDto(Pedido entity);

    /**
     * Mapea una lista de DetallePedido a una lista de DetallePedidoRespuestaDTO.
     * @param detalles La lista de entidades DetallePedido.
     * @return La lista de DTOs de respuesta de detalles de pedido.
     */
    List<DetallePedidoRespuestaDTO> toDetallePedidoDtoList(List<DetallePedido> detalles);

    /**
     * Mapea un DetallePedido a un DetallePedidoRespuestaDTO.
     * Se mapea el ID y nombre del producto.
     * @param detalle La entidad DetallePedido.
     * @return El DTO de respuesta del detalle de pedido.
     */
    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    DetallePedidoRespuestaDTO toDetallePedidoDto(DetallePedido detalle);


    /**
     * Método nombrado para crear una instancia de Usuario a partir de su ID.
     * Esto es un placeholder; en una aplicación real, el servicio de pedidos
     * obtendría la entidad Usuario completa del repositorio.
     * MapStruct no inyecta repositorios directamente en los mappers por diseño,
     * por lo que la lógica de obtención de la entidad Usuario debería estar en el servicio.
     * @param usuarioId El ID del usuario.
     * @return Una instancia de Usuario con solo el ID seteado.
     */
    @Named("mapUsuarioDesdeId")
    default Usuario mapUsuarioDesdeId(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        return usuario;
    }
}
