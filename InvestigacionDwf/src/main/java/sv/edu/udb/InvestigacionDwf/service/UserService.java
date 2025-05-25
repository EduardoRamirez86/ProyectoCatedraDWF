package sv.edu.udb.InvestigacionDwf.service;

import java.util.List;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

public interface UserService {
    List<User> findAllUsers();
}
