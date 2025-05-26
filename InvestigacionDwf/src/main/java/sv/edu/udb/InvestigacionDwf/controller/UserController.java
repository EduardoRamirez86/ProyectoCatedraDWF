package sv.edu.udb.InvestigacionDwf.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.UserCreateRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.UserUpdateAdminRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.UserUpdateRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.UserResponse;
import sv.edu.udb.InvestigacionDwf.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/auth/users")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- Endpoints ya existentes ---
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> listUsers() {
        return userService.findAllUsers();
    }

    @PutMapping("/{id}/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return userService.updateProfile(id, request);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @PathVariable Long id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword
    ) {
        userService.changePassword(id, currentPassword, newPassword);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserProfile(@PathVariable Long id) {
        return userService.getUserProfile(id);
    }

    // --- Nuevos Endpoints para CRUD y Paginación (Administrador) ---

    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> listAllUsersPaginated(
            @PageableDefault(size = 10, sort = "idUser") Pageable pageable
    ) {
        return userService.findAllUsers(pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUsersByAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateAdminRequest request
    ) {
        return userService.updateUsersByAdmin(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}