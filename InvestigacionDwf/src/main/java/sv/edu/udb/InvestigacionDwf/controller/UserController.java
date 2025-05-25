package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.UserUpdateRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.UserResponse;
import sv.edu.udb.InvestigacionDwf.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/auth/users")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> listUsers() {
        return userService.findAllUsers();
    }

    @PutMapping("/{id}/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateProfile(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request
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

}
