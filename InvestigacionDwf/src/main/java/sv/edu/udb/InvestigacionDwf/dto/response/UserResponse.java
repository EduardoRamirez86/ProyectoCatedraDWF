package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse extends RepresentationModel<UserResponse> {
    private Long id;
    private String username;
    private String email;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String roleName;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getIdUser())
                .username(user.getUsername())
                .email(user.getEmail())
                .fechaNacimiento(user.getFechaNacimiento())
                .telefono(user.getTelefono())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }
}