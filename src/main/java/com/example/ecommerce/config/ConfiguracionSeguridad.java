package com.example.ecommerce.config;

import com.example.ecommerce.servicios.ServicioDetallesUsuario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Clase de configuración de seguridad para la aplicación Spring Boot.
 * Habilita la seguridad web y la seguridad a nivel de método.
 */
@Configuration
@EnableWebSecurity // Habilita la seguridad web
@EnableMethodSecurity // Habilita la seguridad a nivel de método (ej. @PreAuthorize)
public class ConfiguracionSeguridad {

    private final ServicioDetallesUsuario servicioDetallesUsuario;

    public ConfiguracionSeguridad(ServicioDetallesUsuario servicioDetallesUsuario) {
        this.servicioDetallesUsuario = servicioDetallesUsuario;
    }

    /**
     * Define el codificador de contraseñas (BCryptPasswordEncoder).
     * @return Una instancia de PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el AuthenticationManager, que se encarga de la autenticación de usuarios.
     * Utiliza un DaoAuthenticationProvider con nuestro ServicioDetallesUsuario y PasswordEncoder.
     * @return Una instancia de AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(servicioDetallesUsuario);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    /**
     * Define la cadena de filtros de seguridad HTTP.
     * Configura la autorización para diferentes endpoints y habilita la autenticación básica.
     * @param http Objeto HttpSecurity para configurar la seguridad.
     * @return Una instancia de SecurityFilterChain.
     * @throws Exception Si ocurre un error de configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF para APIs REST [Non-source: Common practice for REST APIs]
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir acceso público a endpoints de autenticación y registro
                        .requestMatchers(HttpMethod.POST, "/api/autenticacion/registro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/autenticacion/login").permitAll()

                        // Proteger endpoints de gestión de productos para rol ADMIN
                        .requestMatchers("/api/productos/**").hasRole("ADMIN")

                        // Proteger endpoints de pedidos: crear pedido para USER, consultar todos para ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/usuario/**").hasAnyRole("USER", "ADMIN") // User puede ver los suyos, admin puede ver los de cualquiera
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasRole("ADMIN") // Admin puede ver todos los pedidos y pedidos por ID

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.realmName("API Inventario y Pedidos")) // Habilita autenticación HTTP Basic
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Las APIs REST suelen ser sin estado [Non-source: Common practice for REST APIs]

        return http.build();
    }
}

