// RopaService.java
package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.model.entity.Ropa;

import java.util.List;
import java.util.Optional;

public interface RopaService {

    List<Ropa> getAllRopa();

    Optional<Ropa> getRopaById(Long id);

    Ropa createRopa(Ropa ropa);

    Ropa updateRopa(Long id, Ropa ropaDetails);

    void deleteRopa(Long id);
}
