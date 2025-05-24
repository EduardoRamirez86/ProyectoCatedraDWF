package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.TipoProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.TipoProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;

import java.util.Objects; // Importa Objects para las validaciones de nulidad

@Component
public class TipoProductoMapper {

    /**
     * Convierte un DTO de solicitud (TipoProductoRequest) a una entidad TipoProducto.
     * Utiliza el patrón Builder de Lombok para una creación de objeto más legible.
     *
     * @param request El DTO de solicitud que contiene los datos del tipo de producto.
     * @return Una nueva entidad TipoProducto con los datos del request.
     * @throws IllegalArgumentException si el request es nulo o si 'tipo' o 'descripcion' son nulos.
     */
    public TipoProducto toEntity(TipoProductoRequest request) {
        // Valida que el objeto de solicitud no sea nulo.
        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("La solicitud de tipo de producto no puede ser nula.");
        }
        // Valida que los campos esenciales no sean nulos. Se podría añadir .isBlank() si no deben ser cadenas vacías.
        if (Objects.isNull(request.getTipo())) {
            throw new IllegalArgumentException("El tipo de producto no puede ser nulo.");
        }
        if (Objects.isNull(request.getDescripcion())) {
            throw new IllegalArgumentException("La descripción del tipo de producto no puede ser nula.");
        }

        return TipoProducto.builder()
                .tipo(request.getTipo())
                .descripcion(request.getDescripcion())
                .build();
    }

    /**
     * Convierte una entidad TipoProducto a un DTO de respuesta (TipoProductoResponse).
     * Utiliza el patrón Builder de Lombok para una creación de objeto más legible.
     *
     * @param entity La entidad TipoProducto a convertir.
     * @return Un DTO TipoProductoResponse con los datos de la entidad.
     * @throws IllegalArgumentException si la entidad es nula.
     */
    public TipoProductoResponse toResponse(TipoProducto entity) {
        // Valida que la entidad no sea nula antes de intentar mapearla.
        if (Objects.isNull(entity)) {
            throw new IllegalArgumentException("La entidad TipoProducto no puede ser nula para mapear a respuesta.");
        }

        return TipoProductoResponse.builder()
                .idTipoProducto(entity.getIdTipoProducto())
                .tipo(entity.getTipo())
                .descripcion(entity.getDescripcion())
                .build();
    }
}

