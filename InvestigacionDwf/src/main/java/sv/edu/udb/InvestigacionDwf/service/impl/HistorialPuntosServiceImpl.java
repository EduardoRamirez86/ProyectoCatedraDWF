// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/HistorialPuntosServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPuntos;
import sv.edu.udb.InvestigacionDwf.repository.HistorialPuntosRepository;
import sv.edu.udb.InvestigacionDwf.service.HistorialPuntosService;

@Service
@RequiredArgsConstructor
public class HistorialPuntosServiceImpl implements HistorialPuntosService {

    private final HistorialPuntosRepository repo;

    @Override
    public Page<HistorialPuntos> findAllEntities(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public HistorialPuntos getByIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historial no encontrado ID: " + id));
    }
}

