package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.request.UserUpdateRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    /**
     * Obtiene todos los usuarios en forma de DTO de respuesta
     */
    List<UserResponse> findAllUsers();

    /**
     * Actualiza el perfil del usuario (username, email) si la contraseña actual es válida
     */
    UserResponse updateProfile(Long userId, UserUpdateRequest request);

    /**
     * Cambia la contraseña del usuario si la contraseña actual es válida
     */
    void changePassword(Long userId, String currentPassword, String newPassword);

    UserResponse getUserProfile(Long id);

}