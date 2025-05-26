package sv.edu.udb.InvestigacionDwf.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Autowired
    public JwtAuthFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Log para ver la ruta de la solicitud
        System.out.println("JwtAuthFilter - Processing request for: " + request.getRequestURI());

        String path = request.getRequestURI();
        if (path.equals("/auth/login") || path.equals("/auth/register") ||
                path.startsWith("/h2-console") || path.equals("/error")) {
            System.out.println("JwtAuthFilter - Skipping filter for path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization"); // Usar el nombre de variable local 'authHeader'

        // Log para ver el encabezado Authorization
        System.out.println("JwtAuthFilter - Authorization Header: " + authHeader);

        String jwt = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("JwtAuthFilter - Extracted JWT: " + jwt);
        } else {
            System.out.println("JwtAuthFilter - No JWT token found or invalid format in header. Continuing filter chain.");
            // Si no hay token o el formato es incorrecto, no hay autenticación JWT
            filterChain.doFilter(request, response);
            return;
        }

        // Verificación del token y extracción de claims
        if (jwt != null && jwtUtils.validateToken(jwt)) {
            System.out.println("JwtAuthFilter - Token is VALID.");
            try {
                Claims claims = jwtUtils.getClaims(jwt);
                String username = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                String roles = claims.get("roles", String.class);

                System.out.println("JwtAuthFilter - Extracted Username: " + username + ", Roles: " + roles);

                // Verifica si ya hay autenticación en el contexto para evitar sobrescribir
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    CustomUserDetails userDetails = new CustomUserDetails(username, userId, roles);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null, // No password in token based authentication
                                    AuthorityUtils.commaSeparatedStringToAuthorityList(roles)
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("JwtAuthFilter - SecurityContextHolder updated for user: " + username);
                } else {
                    System.out.println("JwtAuthFilter - Username is null or already authenticated. Skipping SecurityContext update.");
                }
            } catch (Exception e) {
                System.out.println("JwtAuthFilter - Error processing JWT claims: " + e.getMessage());
                // Limpiar contexto en caso de error de procesamiento de claims
                SecurityContextHolder.clearContext();
            }
        } else {
            System.out.println("JwtAuthFilter - Token is NULL or INVALID.");
            // Limpiar contexto si el token no es válido o es nulo (después de la extracción)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
    // El método extractToken ya no es necesario, su lógica está integrada en doFilterInternal
}