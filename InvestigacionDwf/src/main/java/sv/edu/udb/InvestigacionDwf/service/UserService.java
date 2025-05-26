package sv.edu.udb.InvestigacionDwf.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sv.edu.udb.InvestigacionDwf.dto.request.UserCreateRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.UserUpdateAdminRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.UserUpdateRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> findAllUsers();

    Page<UserResponse> findAllUsers(Pageable pageable);

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateProfile(Long userId, UserUpdateRequest request);

    UserResponse updateUsersByAdmin(Long userId, UserUpdateAdminRequest request);

    void changePassword(Long userId, String currentPassword, String newPassword);

    UserResponse getUserProfile(Long id);

    void deleteUser(Long userId);
}