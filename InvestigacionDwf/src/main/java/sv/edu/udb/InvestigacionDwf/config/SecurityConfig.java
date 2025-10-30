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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ————————————————
                        // RUTAS PÚBLICAS
                        // ————————————————
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/h2-console/**", "/error").permitAll()
                        // público: listar TODOS los productos y recomendaciones
                        .requestMatchers(HttpMethod.GET,
                                "/auth/producto/all",
                                "/auth/producto/recomendados/**"
                        ).permitAll()
                        // público: listar ROPA, TIPOPRODUCTO, RESEÑAS, PARÁMETROS
                        .requestMatchers(HttpMethod.GET,
                                "/auth/ropa/**",
                                "/auth/producto/**",
                                "/auth/tipoproducto/**",
                                "/auth/resenas/**",
                                "/auth/parametros/**"
                        ).permitAll()

                        // En lugar de la línea genérica, separamos GET, POST, PUT, DELETE
                        .requestMatchers(HttpMethod.GET,    "/auth/direcciones/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.POST,   "/auth/direcciones/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT,    "/auth/direcciones/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, "/auth/direcciones/**").hasAnyRole("ADMIN","USER") // ¡¡LA LÍNEA CLAVE!!
                        // --- ----------------------------------------------------------------- ---


                        // ————————————————
                        // ADMIN Exclusivo: gestión de usuarios y tipos de producto
                        // ————————————————
                        .requestMatchers(HttpMethod.GET,    "/auth/users/paginated").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/auth/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/auth/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/auth/users/{id}/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/auth/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/auth/tipoproducto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/auth/tipoproducto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/auth/tipoproducto/**").hasRole("ADMIN")

                        // ————————————————
                        // ROPA: CRUD para ADMIN y EMPLOYEE
                        // ————————————————
                        .requestMatchers("/auth/ropa/**").hasAnyRole("ADMIN","EMPLOYEE")

                        // ————————————————
                        // PRODUCTO: CRUD para ADMIN y EMPLOYEE (salvo las GET públicas ya declaradas)
                        // ————————————————
                        .requestMatchers("/auth/producto/**").hasAnyRole("ADMIN","EMPLOYEE")

                        // ————————————————
                        // PEDIDOS: CRUD para ADMIN, USER y EMPLOYEE
                        // ————————————————
                        .requestMatchers("/auth/pedido/**").hasAnyRole("ADMIN","USER","EMPLOYEE")
                        .requestMatchers("/auth/historial-pedidos/**").hasAnyRole("ADMIN","USER","EMPLOYEE")

                        // ————————————————
                        // CARRITO y NOTIFICACIONES: ADMIN, USER y EMPLOYEE
                        // ————————————————
                        .requestMatchers("/auth/carrito/**").hasAnyRole("ADMIN","USER","EMPLOYEE")
                        .requestMatchers("/auth/carrito-item/**").hasAnyRole("ADMIN","USER","EMPLOYEE")
                        .requestMatchers("/auth/notificacion/**").hasAnyRole("ADMIN","USER","EMPLOYEE")

                        // ————————————————
                        // RESEÑAS, DIRECCIONES, PUNTOS: ADMIN/USER
                        // ————————————————
                        .requestMatchers(HttpMethod.POST,   "/auth/resenas/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT,    "/auth/resenas/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, "/auth/resenas/**").hasAnyRole("ADMIN","USER")



                        .requestMatchers("/auth/historial-puntos/**").hasAnyRole("ADMIN","USER")

                        // ————————————————
                        // PERFIL PROPIO: ADMIN/USER
                        // ————————————————
                        .requestMatchers(HttpMethod.GET, "/auth/users/{id}").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT, "/auth/users/{id}/profile").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT, "/auth/users/{id}/password").hasAnyRole("ADMIN","USER")

                        // cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions().sameOrigin())
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