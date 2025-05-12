// src/main/java/sv/edu/udb/InvestigacionDwf/service/DireccionService.java
package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.request.DireccionRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.DireccionResponse;

import java.util.List;

public interface DireccionService {
    DireccionResponse save(DireccionRequest req, Long idUser);
    List<DireccionResponse> findByUser(Long idUser);
}
