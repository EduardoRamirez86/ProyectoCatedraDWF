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
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) Habilitar CORS (usa corsConfigurationSource)
                .cors(AbstractHttpConfigurer::disable)
                // 2) Deshabilitar CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 3) Reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // a) Permitir todos los preflights CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // b) Rutas públicas
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/ropa/**", "/auth/compras", "/auth/producto/**", "/auth/tipoproducto/**").permitAll()
                        .requestMatchers("/h2-console/**", "/error").permitAll()
                        // c) Rutas protegidas (ADMIN)
                        .requestMatchers(HttpMethod.POST,   "/auth/ropa/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/auth/ropa/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/auth/ropa/**").hasRole("ADMIN")
                        //Producto(ADMIN)
                        .requestMatchers(HttpMethod.POST,   "/auth/producto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/auth/producto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/auth/producto/**").hasRole("ADMIN")
                        //TipoProducto(ADMIN)
                        .requestMatchers(HttpMethod.POST,   "/auth/tipoproducto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/auth/tipoproducto/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/auth/tipoproducto/**").hasRole("ADMIN")
                        // d) El resto requiere autenticación
                        .anyRequest().authenticated()
                )
                // 4) Desactivar form login
                .formLogin(AbstractHttpConfigurer::disable)
                // 5) Permitir H2 console en iframe sameOrigin
                .headers(headers -> headers.frameOptions().sameOrigin())
                // 6) Agregar filtro JWT
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type")); // ← Debe incluir Authorization :contentReference[oaicite:5]{index=5}
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
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
