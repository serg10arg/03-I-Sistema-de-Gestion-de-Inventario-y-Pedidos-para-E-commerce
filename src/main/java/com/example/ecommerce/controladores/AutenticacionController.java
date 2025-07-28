package com.example.ecommerce.controladores;

import com.example.ecommerce.dto.AutenticacionPeticionDTO;
import com.example.ecommerce.dto.AutenticacionRespuestaDTO;
import com.example.ecommerce.dto.UsuarioRegistroDTO;
import com.example.ecommerce.servicios.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Controlador para la gestión de autenticación y registro de usuarios.
 */
@RestController
@RequestMapping("/api/autenticacion")
public class AutenticacionController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AutenticacionController(AuthService authService,
                                   AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Endpoint para registrar un nuevo usuario en el sistema.
     * @param registroDTO DTO con los datos del nuevo usuario.
     * @return ResponseEntity con un mensaje de éxito o error.
     */
    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO registroDTO) {
        authService.registrarUsuario(registroDTO);
        return new ResponseEntity<>("Usuario registrado exitosamente!", HttpStatus.CREATED);
    }

    /**
     * Endpoint para la autenticación de usuarios (login).
     * @param autenticacionDTO DTO con las credenciales del usuario.
     * @return ResponseEntity con un mensaje de éxito y un token (si se usara JWT).
     */
    @PostMapping("/login")
    public ResponseEntity<AutenticacionRespuestaDTO> autenticarUsuario(@Valid @RequestBody AutenticacionPeticionDTO autenticacionDTO) {
        // Autentica al usuario usando el AuthenticationManager
        Authentication autenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(autenticacionDTO.getNombreUsuario(), autenticacionDTO.getContrasena())
        );

        // Establece la autenticación en el contexto de seguridad de Spring
        SecurityContextHolder.getContext().setAuthentication(autenticacion);

        // Para una implementación básica, simplemente retorna un mensaje de éxito.
        // Si se usara JWT, aquí se generaría y retornaría el token.
        return new ResponseEntity<>(new AutenticacionRespuestaDTO("Autenticación exitosa. ¡Bienvenido!", "no-jwt-token-basico"), HttpStatus.OK);
    }
}

