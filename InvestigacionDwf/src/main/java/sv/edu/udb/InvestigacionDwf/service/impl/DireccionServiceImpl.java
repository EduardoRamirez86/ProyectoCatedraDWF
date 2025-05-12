// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/DireccionServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.DireccionRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.DireccionResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.repository.DireccionRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.service.DireccionService;
import sv.edu.udb.InvestigacionDwf.service.mapper.DireccionMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DireccionServiceImpl implements DireccionService {
    private final DireccionRepository repo;
    private final UserRepository userRepo;
    private final DireccionMapper mapper;

    @Override
    @Transactional
    public DireccionResponse save(DireccionRequest req, Long idUser) {
        User u = userRepo.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Direccion d = mapper.toEntity(req);
        d.setUser(u);
        return mapper.toResponse(repo.save(d));
    }

    @Override
    public List<DireccionResponse> findByUser(Long idUser) {
        return repo.findByUser_IdUser(idUser).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
