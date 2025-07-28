package com.example.ecommerce.servicios;

import com.example.ecommerce.dto.UsuarioRegistroDTO;
import com.example.ecommerce.excepciones.UsuarioYaExisteException;
import com.example.ecommerce.modelo.entidades.Usuario;
import com.example.ecommerce.repositorios.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la lógica de negocio de autenticación y registro.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param registroDTO DTO con los datos del nuevo usuario.
     * @return La entidad Usuario creada.
     * @throws UsuarioYaExisteException si el nombre de usuario ya está en uso.
     */
    @Transactional
    public Usuario registrarUsuario(UsuarioRegistroDTO registroDTO) {
        if (usuarioRepository.existsByNombreUsuario(registroDTO.getNombreUsuario())) {
            throw new UsuarioYaExisteException("El nombre de usuario '" + registroDTO.getNombreUsuario() + "' ya está en uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(registroDTO.getNombreUsuario());
        usuario.setContrasena(passwordEncoder.encode(registroDTO.getContrasena()));
        usuario.setRol(registroDTO.getRol());

        return usuarioRepository.save(usuario);
    }
}