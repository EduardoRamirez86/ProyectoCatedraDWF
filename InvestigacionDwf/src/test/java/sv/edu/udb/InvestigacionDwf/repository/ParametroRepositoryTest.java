package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.Parametro;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ParametroRepositoryTest {

    @Autowired
    private ParametroRepository parametroRepository;

    private Long savedParametroId;

    @BeforeEach
    void init() {
        Parametro parametro = Parametro.builder().clave("IVA").valor("13").descripcion("Impuesto").build();
        parametroRepository.save(parametro);
        savedParametroId = parametro.getIdParametro();
    }

    @AfterEach
    void clean() {
        parametroRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<Parametro> parametro = parametroRepository.findById(savedParametroId);
        assertTrue(parametro.isPresent());
        assertEquals("IVA", parametro.get().getClave());
    }

    @Test
    void testFindAll() {
        List<Parametro> parametros = parametroRepository.findAll();
        assertNotNull(parametros);
        assertFalse(parametros.isEmpty());
    }

    @Test
    void testSave() {
        Parametro parametro = Parametro.builder().clave("DESCUENTO").valor("10").descripcion("Descuento general").build();
        Parametro saved = parametroRepository.save(parametro);
        assertNotNull(saved.getIdParametro());
        assertEquals("DESCUENTO", saved.getClave());
    }

    @Test
    void testUpdate() {
        Optional<Parametro> parametroOpt = parametroRepository.findById(savedParametroId);
        assertTrue(parametroOpt.isPresent());
        Parametro parametro = parametroOpt.get();
        parametro.setValor("15");
        parametroRepository.save(parametro);

        Optional<Parametro> updated = parametroRepository.findById(savedParametroId);
        assertTrue(updated.isPresent());
        assertEquals("15", updated.get().getValor());
    }

    @Test
    void testDelete() {
        Optional<Parametro> parametroOpt = parametroRepository.findById(savedParametroId);
        assertTrue(parametroOpt.isPresent());
        parametroRepository.delete(parametroOpt.get());
        Optional<Parametro> deleted = parametroRepository.findById(savedParametroId);
        assertFalse(deleted.isPresent());
    }
}
