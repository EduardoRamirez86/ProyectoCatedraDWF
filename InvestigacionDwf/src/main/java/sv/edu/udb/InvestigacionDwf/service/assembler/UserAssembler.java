package sv.edu.udb.InvestigacionDwf.service.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.response.UserResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

@Component
public class UserAssembler implements RepresentationModelAssembler<User, UserResponse> {

    @Override
    public UserResponse toModel(User entity) {
        UserResponse dto = UserResponse.fromEntity(entity);

        // Opcional: Añadir enlaces HATEOAS si los necesitas en el futuro
        // dto.add(linkTo(methodOn(UserController.class).getUserProfile(entity.getIdUser())).withSelfRel());
        // dto.add(linkTo(methodOn(UserController.class).listAllUsersPaginated(null)).withRel("users")); // Ejemplo de enlace a la colección

        return dto;
    }
}