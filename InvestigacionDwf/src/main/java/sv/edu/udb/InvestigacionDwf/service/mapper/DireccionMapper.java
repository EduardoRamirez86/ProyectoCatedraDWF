// src/main/java/sv/edu/udb/InvestigacionDwf/service/mapper/DireccionMapper.java
package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.DireccionRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.DireccionResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;

@Component
public class DireccionMapper {

    public Direccion toEntity(DireccionRequest req) {
        Direccion d = new Direccion();
        d.setAlias(req.getAlias());
        d.setCalle(req.getCalle());
        d.setCiudad(req.getCiudad());
        d.setDepartamento(req.getDepartamento());
        d.setLatitud(req.getLatitud());
        d.setLongitud(req.getLongitud());
        return d;
    }

    public DireccionResponse toResponse(Direccion entity) {
        DireccionResponse r = new DireccionResponse();
        r.setIdDireccion(entity.getIdDireccion());
        r.setAlias(entity.getAlias());
        r.setCalle(entity.getCalle());
        r.setCiudad(entity.getCiudad());
        r.setDepartamento(entity.getDepartamento());
        r.setLatitud(entity.getLatitud());
        r.setLongitud(entity.getLongitud());
        return r;
    }
}

