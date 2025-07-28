package com.example.ecommerce.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un recurso solicitado no puede ser encontrado.
 * Mapea a un estado HTTP 404 Not Found.
 */
public class RecursoNoEncontradoException extends RuntimeException {
  public RecursoNoEncontradoException(String recurso, String campo, Object valor) {
    super(String.format("%s no encontrado con %s : '%s'", recurso, campo, valor));
  }

  public RecursoNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
