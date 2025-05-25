// src/main/java/sv/edu/udb/InvestigacionDwf/service/HistorialPuntosService.java
package sv.edu.udb.InvestigacionDwf.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPuntos;

public interface HistorialPuntosService {
    Page<HistorialPuntos> findAllEntities(Pageable pageable);
    HistorialPuntos getByIdEntity(Long id);
}

