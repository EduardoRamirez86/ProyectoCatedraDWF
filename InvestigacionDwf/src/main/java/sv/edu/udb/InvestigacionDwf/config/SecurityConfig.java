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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

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
                        // público: listar TIPOPRODUCTO, RESEÑAS, PARÁMETROS, y GET individual de producto
                        .requestMatchers(HttpMethod.GET,
                                "/auth/ropa/**",
                                "/auth/producto/{id}", // Permitir GET por ID público
                                "/auth/tipoproducto/**",
                                "/auth/resenas/**",
                                "/auth/parametros/**"
                        ).permitAll()

                        // ————————————————
                        // DIRECCIONES: USER/ADMIN
                        // ————————————————
                        .requestMatchers("/auth/direcciones/**").hasAnyRole("ADMIN","USER")

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
                        // ROPA, PRODUCTO, PEDIDOS, CARRITO, NOTIFICACIONES, PUNTOS
                        // ————————————————
                        // ROPA: CRUD para ADMIN y EMPLOYEE
                        .requestMatchers("/auth/ropa/**").hasAnyRole("ADMIN","EMPLOYEE")

                        // PRODUCTO: CRUD para ADMIN y EMPLOYEE (salvo las GET públicas)
                        .requestMatchers(HttpMethod.POST, "/auth/producto/**").hasAnyRole("ADMIN","EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT,  "/auth/producto/**").hasAnyRole("ADMIN","EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/auth/producto/**").hasAnyRole("ADMIN","EMPLOYEE")

                        // PEDIDOS
                        .requestMatchers("/auth/pedido/**").hasAnyRole("ADMIN","USER","EMPLOYEE")
                        .requestMatchers("/auth/historial-pedidos/**").hasAnyRole("ADMIN","USER","EMPLOYEE")

                        // CARRITO y NOTIFICACIONES
                        .requestMatchers("/auth/carrito/**").hasAnyRole("ADMIN","USER","EMPLOYEE")
                        .requestMatchers("/auth/carrito-item/**").hasAnyRole("ADMIN","USER","EMPLOYEE")
                        .requestMatchers("/auth/notificacion/**").hasAnyRole("ADMIN","USER","EMPLOYEE")

                        // RESEÑAS (CRUD)
                        .requestMatchers("/auth/resenas/**").hasAnyRole("ADMIN","USER")

                        // PUNTOS
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


    /**
     * Configuración de CORS para permitir tráfico de Ngrok y localhost.
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // --- 🔑 CAMBIO CLAVE: Permite localhost:3000 y TODOS los subdominios de ngrok-free.dev ---
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://*.ngrok-free.dev"
        ));
        // Nota: Ngrok usa 'https://[subdominio].ngrok-free.dev'
        // ******************************************************

        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        // Permite todos los encabezados (necesario para Authorization y Content-Type)
        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplica a todas las rutas
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
}