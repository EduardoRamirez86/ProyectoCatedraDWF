package sv.edu.udb.InvestigacionDwf.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

    public static class ProductoRequest {
    }
}

