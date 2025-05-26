package sv.edu.udb.InvestigacionDwf; // Asegúrate de que este paquete coincida con el de tu clase principal Application

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // Importa esta anotación

@SpringBootTest
@ActiveProfiles("test") // Activa el perfil "test" para este test de contexto completo
class InvestigacionDwfApplicationTests {

    /**
     * Este test se asegura de que el contexto de Spring Boot se cargue correctamente.
     * Si el contexto se carga sin errores, significa que la configuración de la aplicación
     * es válida y que todos los beans necesarios pueden ser inicializados.
     *
     * Al usar @ActiveProfiles("test"), este test cargará el contexto con las propiedades
     * definidas en application-test.properties, como la base de datos H2 en memoria.
     */
    @Test
    void contextLoads() {
        // No se necesita código aquí. Si el contexto de Spring Boot se carga sin excepciones,
        // el test pasará. Si hay algún problema de configuración (ej. beans no encontrados,
        // dependencias circulares, etc.), el test fallará y se mostrará un error en el log.
    }

}