package com.example.ecommerce.config;

import com.example.ecommerce.modelo.entidades.Producto;
import com.example.ecommerce.modelo.entidades.Usuario;
import com.example.ecommerce.modelo.entidades.enums.Rol;
import com.example.ecommerce.repositorios.ProductoRepository;
import com.example.ecommerce.repositorios.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializerConfig.class);

    @Bean
    @Profile("dev") // Solo se ejecuta cuando el perfil 'dev' está activo
    public CommandLineRunner inicializarDatos(UsuarioRepository usuarioRepository,
                                              ProductoRepository productoRepository,
                                              PasswordEncoder passwordEncoder) {
        return args -> {
            LOG.info("Ejecutando inicializador de datos para el perfil 'dev'...");

            // --- Crear Usuarios ---
            if (usuarioRepository.findByNombreUsuario("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombreUsuario("admin");
                admin.setContrasena(passwordEncoder.encode("adminpass"));
                admin.setRol(Rol.ADMIN);
                usuarioRepository.save(admin);
                LOG.info("Usuario ADMIN creado: admin/adminpass");
            }

            if (usuarioRepository.findByNombreUsuario("usuario").isEmpty()) {
                Usuario user = new Usuario();
                user.setNombreUsuario("usuario");
                user.setContrasena(passwordEncoder.encode("userpass"));
                user.setRol(Rol.USER);
                usuarioRepository.save(user);
                LOG.info("Usuario USER creado: usuario/userpass");
            }

            // --- Crear Productos ---
            if (productoRepository.count() == 0) {
                LOG.info("Creando productos de ejemplo...");

                // CORRECCIÓN: Usar constructor sin argumentos y setters
                Producto p1 = new Producto();
                p1.setNombre("Laptop Gamer");
                p1.setDescripcion("Potente laptop para juegos");
                p1.setPrecio(new BigDecimal("1200.00"));
                p1.setCantidadEnStock(10);

                Producto p2 = new Producto();
                p2.setNombre("Monitor Curvo 27\"");
                p2.setDescripcion("Monitor de alta resolución");
                p2.setPrecio(new BigDecimal("350.50"));
                p2.setCantidadEnStock(25);

                Producto p3 = new Producto();
                p3.setNombre("Teclado Mecánico");
                p3.setDescripcion("Teclado con switches Cherry MX");
                p3.setPrecio(new BigDecimal("99.99"));
                p3.setCantidadEnStock(50);

                Producto p4 = new Producto();
                p4.setNombre("Ratón Inalámbrico");
                p4.setDescripcion("Ratón ergonómico de precisión");
                p4.setPrecio(new BigDecimal("45.00"));
                p4.setCantidadEnStock(100);

                Producto p5 = new Producto();
                p5.setNombre("Auriculares Bluetooth");
                p5.setDescripcion("Auriculares con cancelación de ruido");
                p5.setPrecio(new BigDecimal("150.75"));
                p5.setCantidadEnStock(30);

                productoRepository.saveAll(List.of(p1, p2, p3, p4, p5));
                LOG.info("Productos de ejemplo inicializados.");
            }
        };
    }
}