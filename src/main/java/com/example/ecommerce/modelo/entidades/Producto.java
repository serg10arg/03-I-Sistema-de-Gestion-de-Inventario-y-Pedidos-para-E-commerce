package com.example.ecommerce.modelo.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
//...

@Entity
@Table(name = "productos")
@Data
public class Producto {

    //...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar vacío.")
    @Size(max = 255)
    @Column(nullable = false)
    private String nombre;

    @Size(max = 1000)
    private String descripcion;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero.")
    @Column(nullable = false)
    private BigDecimal precio;

    @Min(value = 0, message = "La cantidad en stock no puede ser negativa.")
    @Column(nullable = false)
    private Integer cantidadEnStock;
}