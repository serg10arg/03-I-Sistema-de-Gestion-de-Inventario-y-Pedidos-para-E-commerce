package com.example.ecommerce.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Clase base para excepciones personalizadas que pueden ser mapeadas a un estado HTTP.
 */
public class ExcepcionPersonalizada extends RuntimeException {
  public ExcepcionPersonalizada(String mensaje) {
    super(mensaje);
  }
}
