package sv.edu.udb.InvestigacionDwf.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sv.edu.udb.InvestigacionDwf.dto.request.LoginRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.RegisterRequest;
import sv.edu.udb.InvestigacionDwf.exception.UserAlreadyExistException;
import sv.edu.udb.InvestigacionDwf.model.Role;
import sv.edu.udb.InvestigacionDwf.model.User;
import sv.edu.udb.InvestigacionDwf.repository.RoleRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.security.jwt.JwtUtils;
import sv.edu.udb.InvestigacionDwf.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/AuthServiceImpl.java
    @Override
    public String register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            logger.error("El usuario ya existe: {}", registerRequest.getUsername());
            throw new UserAlreadyExistException("El usuario ya existe");
        }

        // Asignar ROLE_USER por defecto
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setRole(userRole);

        // Mapear los nuevos campos
        user.setPrimerNombre(registerRequest.getPrimerNombre());
        user.setSegundoNombre(registerRequest.getSegundoNombre());
        user.setPrimerApellido(registerRequest.getPrimerApellido());
        user.setSegundoApellido(registerRequest.getSegundoApellido());
        user.setFechaNacimiento(registerRequest.getFechaNacimiento());
        user.setTelefono(registerRequest.getTelefono());
        user.setDUI(registerRequest.getDUI());
        user.setDireccion(registerRequest.getDireccion());

        userRepository.save(user); // Persistir el usuario

        // Generar y devolver el token JWT con ROLE_USER
        return jwtUtils.generateToken(user.getUsername(), userRole.getName());
    }


    @Override
    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: {}", loginRequest.getUsername());
                    return new RuntimeException("Usuario no encontrado");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.error("Credenciales inválidas para el usuario: {}", loginRequest.getUsername());
            throw new RuntimeException("Credenciales inválidas");
        }

        String role = user.getRole().getName();

        // Generar y devolver el token JWT con el rol de la base de datos
        return jwtUtils.generateToken(user.getUsername(), role);
    }
}