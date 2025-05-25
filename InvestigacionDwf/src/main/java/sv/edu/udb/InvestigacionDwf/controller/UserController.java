package sv.edu.udb.InvestigacionDwf.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Ejemplo de endpoint protegido para listar usuarios
    @GetMapping
    @ResponseStatus(HttpStatus.OK) // Indica que se devuelve un 200 OK
    public List<?> listUsers() { // Usamos List<?> si el tipo exacto del DTO de usuario no está definido aquí
        return userService.findAllUsers();
    }
}
