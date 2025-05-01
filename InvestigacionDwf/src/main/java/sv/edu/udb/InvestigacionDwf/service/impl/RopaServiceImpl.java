// RopaServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.edu.udb.InvestigacionDwf.model.Ropa;
import sv.edu.udb.InvestigacionDwf.repository.RopaRespository;
import sv.edu.udb.InvestigacionDwf.service.RopaService;

import java.util.List;
import java.util.Optional;

@Service
public class RopaServiceImpl implements RopaService {

    private final RopaRespository ropaRepository;

    @Autowired
    public RopaServiceImpl(RopaRespository ropaRepository) {
        this.ropaRepository = ropaRepository;
    }

    @Override
    public List<Ropa> getAllRopa() {
        return ropaRepository.findAll();
    }

    @Override
    public Optional<Ropa> getRopaById(Long id) {
        return ropaRepository.findById(id);
    }

    @Override
    public Ropa createRopa(Ropa ropa) {
        return ropaRepository.save(ropa);
    }

    @Override
    public Ropa updateRopa(Long id, Ropa ropaDetails) {
        return ropaRepository.findById(id)
                .map(ropa -> {
                    ropa.setNombre(ropaDetails.getNombre());
                    ropa.setPrecio(ropaDetails.getPrecio());
                    return ropaRepository.save(ropa);
                })
                .orElseThrow(() -> new RuntimeException("Ropa no encontrada con id: " + id));
    }

    @Override
    public void deleteRopa(Long id) {
        ropaRepository.findById(id)
                .map(ropa -> {
                    ropaRepository.delete(ropa);
                    return ropa;
                })
                .orElseThrow(() -> new RuntimeException("Ropa no encontrada con id: " + id));
    }
}
