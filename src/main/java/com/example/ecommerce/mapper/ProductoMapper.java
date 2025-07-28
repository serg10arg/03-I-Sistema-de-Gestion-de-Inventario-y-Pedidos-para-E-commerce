package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.ProductoActualizacionDTO;
import com.example.ecommerce.dto.ProductoCreacionDTO;
import com.example.ecommerce.dto.ProductoRespuestaDTO;
import com.example.ecommerce.modelo.entidades.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interfaz de mapeo para la entidad Producto y sus DTOs.
 * Usa MapStruct para generar la implementación durante la compilación.
 * El componente de mapeo es "spring" para permitir la inyección de dependencias.
 */
@Mapper(componentModel = "spring")
public interface ProductoMapper {

    /**
     * Mapea un ProductoCreacionDTO a una entidad Producto.
     * @param dto El DTO de creación del producto.
     * @return La entidad Producto mapeada.
     */
    Producto toEntity(ProductoCreacionDTO dto);

    /**
     * Mapea una entidad Producto a un ProductoRespuestaDTO.
     * @param entity La entidad Producto.
     * @return El DTO de respuesta del producto.
     */
    ProductoRespuestaDTO toDto(Producto entity);

    /**
     * Actualiza una entidad Producto existente con los datos de un ProductoActualizacionDTO.
     * Los campos nulos en el DTO no sobrescribirán los valores existentes en la entidad.
     * @param dto El DTO de actualización del producto.
     * @param entity La entidad Producto a actualizar.
     */
    @Mapping(target = "id", ignore = true) // Ignorar el ID en la actualización
    void updateEntityFromDto(ProductoActualizacionDTO dto, @MappingTarget Producto entity);
}
