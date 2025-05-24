package sv.edu.udb.InvestigacionDwf.exception;

import org.springframework.http.HttpStatus; // Importa HttpStatus para el código de estado HTTP
import org.springframework.web.bind.annotation.ResponseStatus; // Importa ResponseStatus para la anotación

/**
 * Excepción personalizada para manejar errores de credenciales inválidas durante la autenticación.
 * Al anotarla con @ResponseStatus(HttpStatus.UNAUTHORIZED), Spring Web automáticamente
 * responderá con un estado HTTP 401 (Unauthorized) cuando esta excepción sea lanzada
 * desde un controlador.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // Mapea esta excepción a un HTTP 401 Unauthorized
public class BadCredentialsException extends RuntimeException {

    /**
     * Constructor que acepta un mensaje para la excepción.
     * @param message El mensaje descriptivo de la excepción.
     */
    public BadCredentialsException(String message) {
        super(message);
    }

    /**
     * Constructor que acepta un mensaje y una causa (otra Throwable) para la excepción.
     * Útil para encadenar excepciones y mantener el rastro original del error.
     * @param message El mensaje descriptivo de la excepción.
     * @param cause La causa original de esta excepción.
     */
    public BadCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
