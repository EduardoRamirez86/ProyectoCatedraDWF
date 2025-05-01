package sv.edu.udb.InvestigacionDwf;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import sv.edu.udb.InvestigacionDwf.security.jwt.JwtUtils;

@TestConfiguration
public class TestConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtils jwtUtils() {
        // Ensure the secret and expiration match the test properties
        return new JwtUtils("test-secret", 3600000L);
    }
}
