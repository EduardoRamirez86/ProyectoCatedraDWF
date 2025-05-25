package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.request.LoginRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest registerRequest);
    String login(LoginRequest loginRequest);
}
