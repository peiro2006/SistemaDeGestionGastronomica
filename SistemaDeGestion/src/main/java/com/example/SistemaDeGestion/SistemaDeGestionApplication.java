package com.example.SistemaDeGestion;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SistemaDeGestionApplication {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public SistemaDeGestionApplication(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(SistemaDeGestionApplication.class, args);
    }

    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute("ALTER TABLE pedido DROP CONSTRAINT IF EXISTS pedido_estado_check");
            System.out.println("Constraint pedido_estado_check eliminado (si existía)");
        } catch (Exception e) {
            System.err.println("No se pudo eliminar constraint: " + e.getMessage());
        }

        try {
            int fix = jdbcTemplate.update(
                "UPDATE pedido SET estado = 'pendiente' WHERE estado IS NULL OR estado NOT IN " +
                "('pendiente','en_preparacion','enviado','entregado','cancelado')"
            );
            if (fix > 0) {
                System.out.println("Estados de pedidos corregidos (filas afectadas): " + fix);
            }
        } catch (Exception e) {
            System.err.println("No se pudieron corregir estados: " + e.getMessage());
        }
    }

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            // Crear usuario admin si no existe
            if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuario WHERE email = 'admin@hamburbesa.com'", Long.class) == 0) {
                String hash = passwordEncoder.encode("Admin123!");
                jdbcTemplate.update(
                    "INSERT INTO usuario (nombre, apellido, email, password, rol, fecha_creacion) VALUES (?, ?, ?, ?, ?, NOW())",
                    "Admin", "Sistema", "admin@hamburbesa.com", hash, "ROLE_ADMIN"
                );
                System.out.println("Usuario admin creado: admin@hamburbesa.com / Admin123!");
            }

            // Crear usuario empleado si no existe
            if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuario WHERE email = 'empleado@hamburbesa.com'", Long.class) == 0) {
                String hash = passwordEncoder.encode("Empleado123!");
                jdbcTemplate.update(
                    "INSERT INTO usuario (nombre, apellido, email, password, rol, fecha_creacion) VALUES (?, ?, ?, ?, ?, NOW())",
                    "Empleado", "Cocina", "empleado@hamburbesa.com", hash, "ROLE_EMPLEADO"
                );
                System.out.println("Usuario empleado creado: empleado@hamburbesa.com / Empleado123!");
            }

            // Crear usuario cliente de prueba
            if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuario WHERE email = 'cliente@hamburbesa.com'", Long.class) == 0) {
                String hash = passwordEncoder.encode("Cliente123!");
                jdbcTemplate.update(
                    "INSERT INTO usuario (nombre, apellido, email, password, rol, fecha_creacion) VALUES (?, ?, ?, ?, ?, NOW())",
                    "Cliente", "Prueba", "cliente@hamburbesa.com", hash, "ROLE_USER"
                );
                System.out.println("Usuario cliente creado: cliente@hamburbesa.com / Cliente123!");
            }

            // Actualizar productos existentes: activar y poner stock si están vacíos
            jdbcTemplate.update(
                "UPDATE producto SET activo = true, stock_actual = COALESCE(stock_actual, 20), stock_minimo = COALESCE(stock_minimo, 5) WHERE activo IS NULL OR stock_actual IS NULL"
            );
            System.out.println("Productos existentes actualizados: activo=true, stock por defecto");

            // Crear productos de prueba si no existen
            Long recetaId = jdbcTemplate.queryForObject("SELECT id_receta FROM receta LIMIT 1", Long.class);
            if (recetaId != null) {
                String[] productos = {
                    "Hamburguesa Clásica|Carne, lechuga, tomate, queso|1500|Hamburguesas|20|5",
                    "Hamburguesa Doble|Doble carne, bacon, queso|2200|Hamburguesas|15|3",
                    "Papas Fritas|Papas caseras con sal|800|Acompañamientos|50|10",
                    "Refresco|Coca Cola 500ml|500|Bebidas|100|20",
                    "Helado|Vainilla con chocolate|1200|Postres|10|5"
                };

                for (String p : productos) {
                    String[] parts = p.split("\\|");
                    String nombre = parts[0];
                    Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM producto WHERE nombre_producto = ?", Long.class, nombre);
                    if (count == 0) {
                        jdbcTemplate.update(
                            "INSERT INTO producto (nombre_producto, descripcion_producto, precio_producto, categoria_producto, stock_actual, stock_minimo, activo, id_receta) VALUES (?, ?, ?, ?, ?, ?, true, ?)",
                            nombre, parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), recetaId
                        );
                        System.out.println("Producto creado: " + nombre);
                    }
                }
            }

            System.out.println("=== Datos de prueba cargados ===");
            System.out.println("Admin: admin@hamburbesa.com / Admin123!");
            System.out.println("Empleado: empleado@hamburbesa.com / Empleado123!");
            System.out.println("Cliente: cliente@hamburbesa.com / Cliente123!");
        };
    }
}