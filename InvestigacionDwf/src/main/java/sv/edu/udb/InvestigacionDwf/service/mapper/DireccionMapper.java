// src/main/java/sv/edu/udb/InvestigacionDwf/service/mapper/DireccionMapper.java
package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.DireccionRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.DireccionResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;

import java.util.Objects; // ¡Importa Objects para las validaciones de nulidad!

@Component
public class DireccionMapper {

    /**
     * Convierte un DTO de solicitud (DireccionRequest) a una entidad Direccion.
     * Utiliza el patrón Builder de Lombok para una creación de objeto más legible.
     * Realiza validaciones básicas de nulidad para campos esenciales.
     *
     * @param req El DTO de solicitud que contiene los datos de la dirección.
     * @return Una nueva entidad Direccion con los datos del request.
     * @throws IllegalArgumentException si el request es nulo o si algún campo esencial es nulo/vacío.
     */
    public Direccion toEntity(DireccionRequest req) {
        // Valida que el objeto de solicitud no sea nulo.
        if (Objects.isNull(req)) {
            throw new IllegalArgumentException("La solicitud de dirección no puede ser nula.");
        }
        // Valida que los campos esenciales no sean nulos o vacíos.
        if (Objects.isNull(req.getAlias()) || req.getAlias().isBlank()) {
            throw new IllegalArgumentException("El alias de la dirección no puede ser nulo o vacío.");
        }
        if (Objects.isNull(req.getCalle()) || req.getCalle().isBlank()) {
            throw new IllegalArgumentException("La calle de la dirección no puede ser nula o vacía.");
        }
        if (Objects.isNull(req.getCiudad()) || req.getCiudad().isBlank()) {
            throw new IllegalArgumentException("La ciudad de la dirección no puede ser nula o vacía.");
        }
        if (Objects.isNull(req.getDepartamento()) || req.getDepartamento().isBlank()) {
            throw new IllegalArgumentException("El departamento de la dirección no puede ser nulo o vacío.");
        }
        // No validamos latitud/longitud aquí si no son estrictamente obligatorias o si el servicio ya las validó.
        // Sin embargo, si un valor es nulo en el request, el builder lo asignará como nulo en la entidad, lo cual está bien.

        return Direccion.builder()
                .alias(req.getAlias())
                .calle(req.getCalle())
                .ciudad(req.getCiudad())
                .departamento(req.getDepartamento())
                .latitud(req.getLatitud())
                .longitud(req.getLongitud())
                .build();
    }



    /**
     * Convierte una entidad Direccion a un DTO de respuesta (DireccionResponse).
     * Utiliza el patrón Builder de Lombok para una creación de objeto más legible.
     * Realiza validación básica de nulidad para la entidad.
     *
     * @param entity La entidad Direccion a convertir.
     * @return Un DTO DireccionResponse con los datos de la entidad.
     * @throws IllegalArgumentException si la entidad es nula.
     */
    public DireccionResponse toResponse(Direccion entity) {
        // Valida que la entidad no sea nula antes de intentar mapearla.
        if (Objects.isNull(entity)) {
            throw new IllegalArgumentException("La entidad Direccion no puede ser nula para mapear a respuesta.");
        }

        return DireccionResponse.builder()
                .idDireccion(entity.getIdDireccion())
                .alias(entity.getAlias())
                .calle(entity.getCalle())
                .ciudad(entity.getCiudad())
                .departamento(entity.getDepartamento())
                .latitud(entity.getLatitud())
                .longitud(entity.getLongitud())
                .build();
    }



    /**
     * Actualiza una entidad Direccion existente con los datos de un DTO de solicitud.
     * Esta es una alternativa al mapeo manual en el servicio, si se prefiere que el mapper maneje la actualización.
     * Utiliza `Objects.nonNull()` para actualizar solo los campos que no son nulos en el request,
     * permitiendo actualizaciones parciales.
     *
     * @param req El DTO de solicitud con los datos a actualizar.
     * @param targetDireccion La entidad Direccion a la que se aplicarán los cambios.
     */
    public void updateEntityFromRequest(DireccionRequest req, Direccion targetDireccion) {
        if (Objects.isNull(req)) {
            throw new IllegalArgumentException("La solicitud de actualización no puede ser nula.");
        }
        if (Objects.isNull(targetDireccion)) {
            throw new IllegalArgumentException("La entidad Dirección a actualizar no puede ser nula.");
        }

        // Actualiza solo si el campo en el request no es nulo y no está vacío (para Strings)
        if (Objects.nonNull(req.getAlias()) && !req.getAlias().isBlank()) {
            targetDireccion.setAlias(req.getAlias());
        }
        if (Objects.nonNull(req.getCalle()) && !req.getCalle().isBlank()) {
            targetDireccion.setCalle(req.getCalle());
        }
        if (Objects.nonNull(req.getCiudad()) && !req.getCiudad().isBlank()) {
            targetDireccion.setCiudad(req.getCiudad());
        }
        if (Objects.nonNull(req.getDepartamento()) && !req.getDepartamento().isBlank()) {
            targetDireccion.setDepartamento(req.getDepartamento());
        }
        if (Objects.nonNull(req.getLatitud())) {
            targetDireccion.setLatitud(req.getLatitud());
        }
        if (Objects.nonNull(req.getLongitud())) {
            targetDireccion.setLongitud(req.getLongitud());
        }
    }
}

