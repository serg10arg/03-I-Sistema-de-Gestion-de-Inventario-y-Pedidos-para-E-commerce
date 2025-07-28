package com.example.ecommerce.servicios;

import com.example.ecommerce.modelo.entidades.Usuario;
import com.example.ecommerce.repositorios.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio de detalles de usuario que implementa UserDetailsService de Spring Security.
 * Utilizado para cargar los detalles de un usuario durante el proceso de autenticación.
 */
@Service
public class ServicioDetallesUsuario implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public ServicioDetallesUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga los detalles de un usuario por su nombre de usuario.
     * @param nombreUsuario El nombre de usuario a buscar.
     * @return Un objeto UserDetails que representa al usuario.
     * @throws UsernameNotFoundException Si el usuario no es encontrado.
     */
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        // Busca el usuario en el repositorio por su nombre de usuario.
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario));
        // Devuelve la entidad Usuario, que ya implementa UserDetails.
        return usuario;
    }
}
