package sv.edu.udb.InvestigacionDwf.dto.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    // Campos adicionales de User
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String DUI;
    private String direccion;
}

