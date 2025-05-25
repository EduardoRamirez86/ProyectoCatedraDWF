package sv.edu.udb.InvestigacionDwf.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String currentPassword;
    private String newUsername;
    private String newEmail;
}
