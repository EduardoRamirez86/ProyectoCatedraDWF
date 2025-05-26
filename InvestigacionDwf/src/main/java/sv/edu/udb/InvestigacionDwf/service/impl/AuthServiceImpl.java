package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor; // Usamos Lombok para el constructor
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importamos Transactional
import sv.edu.udb.InvestigacionDwf.dto.request.LoginRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.RegisterRequest;
import sv.edu.udb.InvestigacionDwf.exception.UserAlreadyExistException;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException; // Importamos ResourceNotFoundException
import sv.edu.udb.InvestigacionDwf.exception.BadCredentialsException; // Creamos una nueva excepción personalizada para credenciales inválidas
import sv.edu.udb.InvestigacionDwf.model.entity.Role;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.repository.RoleRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.security.jwt.JwtUtils;
import sv.edu.udb.InvestigacionDwf.service.AuthService;

import java.util.Objects; // Importamos Objects para las validaciones
import java.time.LocalDate; // Para validar fecha de nacimiento

@Service
@RequiredArgsConstructor // Genera un constructor con todos los campos 'final', inyectando dependencias.
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // El constructor se genera automáticamente por @RequiredArgsConstructor, así que lo puedes omitir.
    // public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
    //     this.userRepository = userRepository;
    //     this.roleRepository = roleRepository;
    //     this.passwordEncoder = passwordEncoder;
    //     this.jwtUtils = jwtUtils;
    // }

    @Override
    @Transactional // La operación de registro debe ser transaccional
    public String register(RegisterRequest registerRequest) {
        // --- Validaciones de entrada ---
        if (Objects.isNull(registerRequest)) {
            throw new IllegalArgumentException("Los datos de registro no pueden ser nulos.");
        }
        if (Objects.isNull(registerRequest.getUsername()) || registerRequest.getUsername().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (Objects.isNull(registerRequest.getEmail()) || registerRequest.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (Objects.isNull(registerRequest.getPassword()) || registerRequest.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (Objects.isNull(registerRequest.getPrimerNombre()) || registerRequest.getPrimerNombre().isBlank()) {
            throw new IllegalArgumentException("El primer nombre es obligatorio.");
        }
        // Puedes añadir más validaciones para otros campos si son obligatorios (ej. Dui, teléfono, fechaNacimiento)
        if (Objects.isNull(registerRequest.getDui()) || registerRequest.getDui().isBlank()) {
            throw new IllegalArgumentException("El DUI es obligatorio.");
        }
        if (Objects.isNull(registerRequest.getFechaNacimiento())) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        // Ejemplo de validación de edad mínima (asumiendo 18 años)
        if (registerRequest.getFechaNacimiento().isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Debes tener al menos 18 años para registrarte.");
        }


        // --- Validación de existencia de usuario/correo ---
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            logger.warn("Intento de registro con usuario existente: {}", registerRequest.getUsername());
            throw new UserAlreadyExistException("El nombre de usuario '" + registerRequest.getUsername() + "' ya existe.");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.warn("Intento de registro con correo existente: {}", registerRequest.getEmail());
            throw new UserAlreadyExistException("El correo electrónico '" + registerRequest.getEmail() + "' ya está registrado.");
        }

        // --- Asignación de rol ---
        // Se utiliza ResourceNotFoundException para indicar que el rol no existe.
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Rol 'ROLE_USER' no encontrado en el sistema. Contacte al administrador."));

        // --- Creación de usuario ---
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword())) // Codifica la contraseña
                .email(registerRequest.getEmail())
                .role(userRole)
                .primerNombre(registerRequest.getPrimerNombre())
                .segundoNombre(registerRequest.getSegundoNombre()) // Puede ser nulo o vacío
                .primerApellido(registerRequest.getPrimerApellido())
                .segundoApellido(registerRequest.getSegundoApellido()) // Puede ser nulo o vacío
                .fechaNacimiento(registerRequest.getFechaNacimiento())
                .telefono(registerRequest.getTelefono())
                .DUI(registerRequest.getDui())
                .direccion(registerRequest.getDireccion()) // Puede ser nulo o vacío (si la dirección se gestiona aparte)
                .build();

        userRepository.save(user); // Guarda el nuevo usuario en la base de datos
        logger.info("Usuario registrado exitosamente: {}", user.getUsername());

        // Genera y devuelve el token JWT para el nuevo usuario
        return jwtUtils.generateToken(user.getUsername(), user.getIdUser(), userRole.getName());
    }

    @Override
    @Transactional(readOnly = true) // El login es una operación de solo lectura
    public String login(LoginRequest loginRequest) {
        // --- Validaciones de entrada ---
        if (Objects.isNull(loginRequest)) {
            throw new IllegalArgumentException("Los datos de login no pueden ser nulos.");
        }
        if (Objects.isNull(loginRequest.getUsername()) || loginRequest.getUsername().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio para el login.");
        }
        if (Objects.isNull(loginRequest.getPassword()) || loginRequest.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria para el login.");
        }

        // --- Autenticación de usuario ---
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Intento de login fallido: Usuario '{}' no encontrado.", loginRequest.getUsername());
                    // Usamos BadCredentialsException para ocultar si fue el usuario o la contraseña incorrecta
                    return new BadCredentialsException("Credenciales inválidas(Usuario no encontrado).");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Intento de login fallido para usuario '{}': Contraseña incorrecta.", loginRequest.getUsername());
            throw new BadCredentialsException("Credenciales inválidas(Contraseña mala).");
        }

        String role = user.getRole().getName(); // Obtiene el nombre del rol del usuario
        logger.info("Login exitoso para el usuario: {}", user.getUsername());

        // Genera y devuelve el token JWT
        return jwtUtils.generateToken(user.getUsername(), user.getIdUser(), role);
    }
}
