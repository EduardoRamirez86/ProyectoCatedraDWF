package sv.edu.udb.InvestigacionDwf.service.mapper;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-26T05:03:38-0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CarritoMapperImpl extends CarritoMapper {

    @Override
    public Carrito toEntity(CarritoRequest request) {
        if ( request == null ) {
            return null;
        }

        Carrito.CarritoBuilder carrito = Carrito.builder();

        carrito.user( Objects.nonNull(request.getIdUser()) ? userRepository.getReferenceById(request.getIdUser()) : null );
        carrito.fechaCreacion( java.time.LocalDateTime.now() );

        return carrito.build();
    }

    @Override
    public CarritoResponse toResponse(Carrito carrito) {
        if ( carrito == null ) {
            return null;
        }

        CarritoResponse.CarritoResponseBuilder carritoResponse = CarritoResponse.builder();

        carritoResponse.idUser( Objects.requireNonNull( carritoUserIdUser( carrito ) ) );
        carritoResponse.idCarrito( Objects.requireNonNull( carrito.getIdCarrito() ) );
        carritoResponse.fechaCreacion( Objects.requireNonNull( carrito.getFechaCreacion() ) );

        return carritoResponse.build();
    }

    @Override
    public void updateEntityFromRequest(CarritoRequest request, Carrito carrito) {
        if ( request == null ) {
            return;
        }

        carrito.setUser( Objects.nonNull(request.getIdUser()) ? userRepository.getReferenceById(request.getIdUser()) : carrito.getUser() );
    }

    private Long carritoUserIdUser(Carrito carrito) {
        User user = carrito.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getIdUser();
    }
}
