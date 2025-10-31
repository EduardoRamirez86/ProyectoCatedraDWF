package sv.edu.udb.InvestigacionDwf.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.annotation.Nullable;

/**
 * DTO para actualización parcial de perfil de usuario.
 * Permite actualizar individualmente username, email o teléfono,
 * siempre exigiendo currentPassword por seguridad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private String currentPassword; // requerido siempre

    @Nullable
    private String newUsername; // puede ser null si no se quiere cambiar

    @Nullable
    private String newEmail; // puede ser null si no se quiere cambiar

    @Nullable
    private String telefono; // se agrega para poder actualizar el teléfono también
}
