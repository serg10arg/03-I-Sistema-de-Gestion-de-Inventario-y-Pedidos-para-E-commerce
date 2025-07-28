package com.example.ecommerce.modelo.entidades;

import com.example.ecommerce.modelo.entidades.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private String contrasena; // Esta contraseña DEBE estar hasheada (ej. BCrypt)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // --- CAMPOS ADICIONALES PARA UN ESTADO DE CUENTA REAL ---
    private boolean cuentaExpirada = false;
    private boolean cuentaBloqueada = false;
    private boolean credencialesExpiradas = false;
    private boolean habilitado = true;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        return nombreUsuario;
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !cuentaExpirada; // Devuelve el estado real
    }

    @Override
    public boolean isAccountNonLocked() {
        return !cuentaBloqueada; // Devuelve el estado real
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credencialesExpiradas; // Devuelve el estado real
    }

    @Override
    public boolean isEnabled() {
        return habilitado; // Devuelve el estado real
    }
}