// src/main/java/sv/edu/udb/InvestigacionDwf/service/mapper/CarritoMapper.java
package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;
import sv.edu.udb.InvestigacionDwf.model.Carrito;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CarritoMapper {

    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "user", expression = "java(userRepository.getReferenceById(request.getIdUser()))")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    public abstract Carrito toEntity(CarritoRequest request);

    @Mapping(target = "idUser", source = "user.idUser")
    @Mapping(target = "items", ignore = true) // items se pueden poblar aparte
    public abstract CarritoResponse toResponse(Carrito carrito);
}

