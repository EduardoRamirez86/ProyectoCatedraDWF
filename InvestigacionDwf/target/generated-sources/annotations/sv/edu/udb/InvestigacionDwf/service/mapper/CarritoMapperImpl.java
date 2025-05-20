package sv.edu.udb.InvestigacionDwf.service.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-20T13:53:26-0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class CarritoMapperImpl extends CarritoMapper {

    @Override
    public Carrito toEntity(CarritoRequest request) {
        if ( request == null ) {
            return null;
        }

        Carrito.CarritoBuilder carrito = Carrito.builder();

        carrito.user( userRepository.getReferenceById(request.getIdUser()) );
        carrito.fechaCreacion( java.time.LocalDateTime.now() );

        return carrito.build();
    }

    @Override
    public CarritoResponse toResponse(Carrito carrito) {
        if ( carrito == null ) {
            return null;
        }

        CarritoResponse.CarritoResponseBuilder carritoResponse = CarritoResponse.builder();

        carritoResponse.idUser( carritoUserIdUser( carrito ) );
        carritoResponse.idCarrito( carrito.getIdCarrito() );
        carritoResponse.fechaCreacion( carrito.getFechaCreacion() );

        return carritoResponse.build();
    }

    private Long carritoUserIdUser(Carrito carrito) {
        User user = carrito.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getIdUser();
    }
}
