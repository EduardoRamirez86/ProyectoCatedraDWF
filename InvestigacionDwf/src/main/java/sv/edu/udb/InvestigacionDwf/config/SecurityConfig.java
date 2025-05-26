package sv.edu.udb.InvestigacionDwf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sv.edu.udb.InvestigacionDwf.security.jwt.JwtAuthFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita la seguridad a nivel de método con @PreAuthorize, etc.
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; // Asegúrate de que JwtAuthFilter esté correctamente definido e inyectable

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) Habilitar CORS (usa corsConfigurationSource)
                // Se usa el Bean corsConfigurationSource definido abajo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 2) Deshabilitar CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 3) Reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // a) Permitir todos los preflights CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // b) Rutas públicas (sin autenticación)
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
                        // Rutas de Swagger/OpenAPI
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api-docs.yaml"
                        ).permitAll()
                        // Rutas GET de acceso público (información general de productos, etc.)
                        .requestMatchers(HttpMethod.GET,
                                "/auth/ropa/**",
                                "/auth/producto/**",
                                "/auth/tipoproducto/**",
                                "/auth/resenas/**", // GET para reseñas puede ser público
                                "/auth/parametros/**" // GET para parámetros puede ser público
                        ).permitAll()

                        // Acceso a la consola H2 y rutas de error
                        .requestMatchers("/h2-console/**", "/error").permitAll()

                        // c) Rutas protegidas (ADMIN)
                        // Gestión de Usuarios (ADMIN)
                        .requestMatchers(HttpMethod.GET,    "/auth/users/paginated").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/auth/users").hasRole("ADMIN") // listUsers() (no paginado)
                        .requestMatchers(HttpMethod.POST,   "/auth/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/auth/users/{id}/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/auth/users/{id}").hasRole("ADMIN")

                        // Gestión de Ropa (ADMIN)
                        .requestMatchers(HttpMethod.POST,   "/auth/ropa/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/auth/ropa/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/auth/ropa/**").hasRole("ADMIN")
                        // Gestión de Productos (ADMIN)
                        .requestMatchers(HttpMethod.POST,   "/auth/producto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/auth/producto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/auth/producto/**").hasRole("ADMIN")
                        // Gestión de Tipo de Producto (ADMIN)
                        .requestMatchers(HttpMethod.POST,   "/auth/tipoproducto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/auth/tipoproducto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/auth/tipoproducto/**").hasRole("ADMIN")

                        // Rutas protegidas (ADMIN o USER)
                        // Pedidos (ADMIN/USER) - Aplicar a todos los métodos
                        .requestMatchers("/auth/pedido/**").hasAnyRole("ADMIN","USER")
                        // Carrito (ADMIN/USER) - Aplicar a todos los métodos
                        .requestMatchers("/auth/carrito/**").hasAnyRole("ADMIN","USER")
                        // CarritoItem (ADMIN/USER) - Aplicar a todos los métodos
                        .requestMatchers("/auth/carrito-item/**").hasAnyRole("ADMIN","USER")
                        // Reseñas (ADMIN/USER) - POST, PUT, DELETE (GET ya es público)
                        .requestMatchers(HttpMethod.POST,   "/auth/resenas/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT,    "/auth/resenas/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, "/auth/resenas/**").hasAnyRole("ADMIN","USER")
                        // Notificaciones (ADMIN/USER) - Aplicar a todos los métodos
                        .requestMatchers("/auth/notificacion/**").hasAnyRole("ADMIN","USER")
                        // Direcciones (ADMIN/USER) - Aplicar a todos los métodos
                        .requestMatchers("/auth/direcciones/**").hasAnyRole("ADMIN","USER")
                        // Historial de Puntos (ADMIN/USER) - Aplicar a todos los métodos
                        .requestMatchers("/auth/historial-puntos/**").hasAnyRole("ADMIN","USER")
                        // Historial de Pedidos (ADMIN/USER) - Aplicar a todos los métodos
                        .requestMatchers("/auth/historial-pedidos/**").hasAnyRole("ADMIN","USER")


                        // Acciones personales de Usuario (USER/ADMIN)
                        .requestMatchers(HttpMethod.GET, "/auth/users/{id}").hasAnyRole("ADMIN", "USER") // Obtener perfil propio
                        .requestMatchers(HttpMethod.PUT, "/auth/users/{id}/profile").hasAnyRole("ADMIN", "USER") // Actualizar perfil propio
                        .requestMatchers(HttpMethod.PUT, "/auth/users/{id}/password").hasAnyRole("ADMIN", "USER") // Cambiar contraseña

                        // d) Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                // 4) Desactivar el formulario de login predeterminado de Spring Security
                .formLogin(AbstractHttpConfigurer::disable)
                // 5) Permitir H2 console en iframe (necesario para ver la consola de H2 en el navegador)
                .headers(headers -> headers.frameOptions().sameOrigin())
                // 6) Agregar el filtro JWT antes del filtro de autenticación de nombre de usuario/contraseña
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Puedes añadir más orígenes si es necesario
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        // *** CAMBIA ESTA LÍNEA: Permite todos los encabezados ***
        config.setAllowedHeaders(List.of("*"));
        // ******************************************************
        config.setAllowCredentials(true); // Permite el envío de cookies y credenciales

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplica esta configuración a todas las rutas
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // Necesitas un UserDetailsService configurado para que AuthenticationManager funcione correctamente
        // Aquí se asume que tu UserDetailsService ya está configurado (por ejemplo, en otra clase o implícitamente)
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
}