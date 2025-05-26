package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.Ropa;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RopaRepositoryTest {

    @Autowired
    private RopaRespository ropaRespository;

    private Long savedRopaId;

    @BeforeEach
    void init() {
        Ropa ropa = new Ropa();
        ropa.setNombre("Camisa");
        ropa.setPrecio(10.0);
        ropaRespository.save(ropa);
        savedRopaId = ropa.getIdRopa();
    }

    @AfterEach
    void clean() {
        ropaRespository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<Ropa> ropa = ropaRespository.findById(savedRopaId);
        assertTrue(ropa.isPresent());
        assertEquals("Camisa", ropa.get().getNombre());
    }

    @Test
    void testFindAll() {
        List<Ropa> ropas = ropaRespository.findAll();
        assertNotNull(ropas);
        assertFalse(ropas.isEmpty());
    }

    @Test
    void testSave() {
        Ropa ropa = new Ropa();
        ropa.setNombre("Pantalón");
        ropa.setPrecio(20.0);
        Ropa saved = ropaRespository.save(ropa);
        assertNotNull(saved.getIdRopa());
        assertEquals("Pantalón", saved.getNombre());
    }

    @Test
    void testUpdate() {
        Optional<Ropa> ropaOpt = ropaRespository.findById(savedRopaId);
        assertTrue(ropaOpt.isPresent());
        Ropa ropa = ropaOpt.get();
        ropa.setPrecio(15.0);
        ropaRespository.save(ropa);

        Optional<Ropa> updated = ropaRespository.findById(savedRopaId);
        assertTrue(updated.isPresent());
        assertEquals(15.0, updated.get().getPrecio());
    }

    @Test
    void testDelete() {
        Optional<Ropa> ropaOpt = ropaRespository.findById(savedRopaId);
        assertTrue(ropaOpt.isPresent());
        ropaRespository.delete(ropaOpt.get());
        Optional<Ropa> deleted = ropaRespository.findById(savedRopaId);
        assertFalse(deleted.isPresent());
    }
}
