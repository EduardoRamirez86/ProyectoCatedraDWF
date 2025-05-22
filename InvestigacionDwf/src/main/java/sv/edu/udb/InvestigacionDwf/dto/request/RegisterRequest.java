package sv.edu.udb.InvestigacionDwf.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

import lombok.Data;
import sv.edu.udb.InvestigacionDwf.controller.validation.Dui;
import sv.edu.udb.InvestigacionDwf.controller.validation.PhoneNumber;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 5, max = 20, message = "El nombre de usuario tiene que llevar entre 5 y 20 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña tiene que contener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El email debe ser un formato valido")
    private String email;

    @NotBlank(message = "El primer nombre es necesario")
    private String primerNombre;

    private String segundoNombre;

    @NotBlank(message = "El primer apellido es requerido")
    private String primerApellido;

    private String segundoApellido;

    @Past
    @NotNull(message = "La fecha de nacimiento es necesaria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El numero de telefono no puede estar vacio")
    @PhoneNumber(message = "Numero de telefono invalido (valido = 1234-5678)", pattern = "^\\d{4}-\\d{4}$")
    private String telefono;

    @NotNull(message = "El Dui no puede estar vacio")
    @Dui(message = "El formato del numero de Dui no es valido(valido = 12345678-9)", pattern = "^\\d{8}-\\d$")
    private String dui;

    @NotBlank(message = "Por favor agrega una direccion")
    private String direccion;
}
