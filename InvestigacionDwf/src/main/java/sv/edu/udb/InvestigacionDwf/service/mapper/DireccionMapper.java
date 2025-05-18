// src/main/java/sv/edu/udb/InvestigacionDwf/service/mapper/DireccionMapper.java
package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.DireccionRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.DireccionResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;

@Component
public class DireccionMapper {

    public Direccion toEntity(DireccionRequest req) {
        return Direccion.builder()
                .alias(req.getAlias())
                .calle(req.getCalle())
                .ciudad(req.getCiudad())
                .departamento(req.getDepartamento())
                .latitud(req.getLatitud())
                .longitud(req.getLongitud())
                .build();
    }

    public DireccionResponse toResponse(Direccion entity) {
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
}


