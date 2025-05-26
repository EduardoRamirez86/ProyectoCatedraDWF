package sv.edu.udb.InvestigacionDwf.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// import org.springframework.data.web.PagedResourcesAssembler; // ELIMINAR ESTA IMPORTACIÓN si no se usa
// import org.springframework.hateoas.PagedModel; // ELIMINAR ESTA IMPORTACIÓN si no se usa
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.UserCreateRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.UserUpdateAdminRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.UserUpdateRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.UserResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.exception.DuplicateResourceException;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.repository.RoleRepository;
import sv.edu.udb.InvestigacionDwf.service.UserService;
import sv.edu.udb.InvestigacionDwf.service.assembler.UserAssembler;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAssembler userAssembler;

    // ELIMINAR la inyección de PagedResourcesAssembler del constructor
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, UserAssembler userAssembler) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAssembler = userAssembler;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userAssembler::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userAssembler::toModel); // Usamos Page.map() con el UserAssembler
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("El nombre de usuario ya existe: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("El email ya existe: " + request.getEmail());
        }

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + request.getRoleName()));

        User user = User.builder()
                .primerNombre(request.getPrimerNombre())
                .segundoNombre(request.getSegundoNombre())
                .primerApellido(request.getPrimerApellido())
                .segundoApellido(request.getSegundoApellido())
                .fechaNacimiento(request.getFechaNacimiento())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .DUI(request.getDUI())
                .direccion(request.getDireccion())
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        return userAssembler.toModel(savedUser);
    }

    @Override
    public UserResponse updateProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        if (request.getNewUsername() != null && !request.getNewUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getNewUsername())) {
                throw new DuplicateResourceException("El nuevo nombre de usuario ya existe: " + request.getNewUsername());
            }
            user.setUsername(request.getNewUsername());
        }
        if (request.getNewEmail() != null && !request.getNewEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getNewEmail())) {
                throw new DuplicateResourceException("El nuevo email ya existe: " + request.getNewEmail());
            }
            user.setEmail(request.getNewEmail());
        }

        User updated = userRepository.save(user);
        return userAssembler.toModel(updated);
    }

    @Override
    public UserResponse updateUsersByAdmin(Long userId, UserUpdateAdminRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (request.getPrimerNombre() != null) user.setPrimerNombre(request.getPrimerNombre());
        if (request.getSegundoNombre() != null) user.setSegundoNombre(request.getSegundoNombre());
        if (request.getPrimerApellido() != null) user.setPrimerApellido(request.getPrimerApellido());
        if (request.getSegundoApellido() != null) user.setSegundoApellido(request.getSegundoApellido());
        if (request.getFechaNacimiento() != null) user.setFechaNacimiento(request.getFechaNacimiento());
        if (request.getTelefono() != null) user.setTelefono(request.getTelefono());
        if (request.getDUI() != null) user.setDUI(request.getDUI());
        if (request.getDireccion() != null) user.setDireccion(request.getDireccion());

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("El nombre de usuario ya existe: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("El email ya existe: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getRoleName() != null) {
            Role newRole = roleRepository.findByName(request.getRoleName())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + request.getRoleName()));
            user.setRole(newRole);
        }

        User updatedUser = userRepository.save(user);
        return userAssembler.toModel(updatedUser);
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return userAssembler.toModel(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }
        userRepository.deleteById(userId);
    }
}