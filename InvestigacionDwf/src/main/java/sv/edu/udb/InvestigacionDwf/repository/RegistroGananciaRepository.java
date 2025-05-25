package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.edu.udb.InvestigacionDwf.model.entity.RegistroGanancia;

@Repository
public interface RegistroGananciaRepository extends JpaRepository<RegistroGanancia, Long> {
    // Puedes añadir métodos de consulta específicos aquí si los necesitas
}
