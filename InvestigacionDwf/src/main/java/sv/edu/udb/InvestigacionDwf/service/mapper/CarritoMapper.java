package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired; // Mantener por ahora si se usa @Autowired de MapStruct
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;

import java.util.Objects; // Importar Objects

// Usando MapStruct para características avanzadas de mapeo e inyección de dependencias dentro de los mappers.
// MapStruct generará una clase de implementación para esta clase abstracta.
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {java.time.LocalDateTime.class, Objects.class}) // Añadir Objects.class a 'uses' para usar Objects.nonNull()
public abstract class CarritoMapper {

    // MapStruct maneja este @Autowired dentro de su implementación generada para componentModel = "spring"
    @Autowired
    protected UserRepository userRepository;

    /**
     * Convierte un DTO CarritoRequest a una entidad Carrito.
     * Asume que el 'idUser' en la solicitud es válido y lo mapea a una entidad User.
     * Establece la fecha de creación a la LocalDateTime actual.
     *
     * @param request El DTO CarritoRequest.
     * @return Una entidad Carrito.
     * @throws IllegalArgumentException Si la solicitud o los campos esenciales son nulos.
     */
    @Mapping(target = "user", expression = "java(Objects.nonNull(request.getIdUser()) ? userRepository.getReferenceById(request.getIdUser()) : null)")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    public abstract Carrito toEntity(CarritoRequest request);

    /**
     * Convierte una entidad Carrito a un DTO CarritoResponse.
     * Mapea el ID de usuario de la entidad User asociada.
     * Ignora la colección 'items' ya que puede ser poblada por separado para evitar problemas de N+1.
     *
     * @param carrito La entidad Carrito.
     * @return Un DTO CarritoResponse.
     * @throws IllegalArgumentException Si la entidad carrito o su usuario asociado es nulo.
     */
    @Mapping(target = "idUser", source = "user.idUser")
    @Mapping(target = "items", ignore = true) // los ítems pueden ser poblados por separado si es necesario
    public abstract CarritoResponse toResponse(Carrito carrito);

    /**
     * Actualiza una entidad Carrito existente con datos de un DTO CarritoRequest.
     * Ignora las propiedades nulas en la solicitud.
     * Solo actualiza el usuario si idUser se proporciona y no es nulo en la solicitud.
     *
     * @param request El DTO CarritoRequest con datos de actualización.
     * @param carrito La entidad Carrito existente que se va a actualizar.
     * @throws IllegalArgumentException Si la solicitud o el carrito de destino son nulos.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", expression = "java(Objects.nonNull(request.getIdUser()) ? userRepository.getReferenceById(request.getIdUser()) : carrito.getUser())")
    @Mapping(target = "fechaCreacion", ignore = true) // La fecha de creación no debería actualizarse aquí
    public abstract void updateEntityFromRequest(CarritoRequest request, @MappingTarget Carrito carrito);
}
