package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TipoProductoRepositoryTest {

    @Autowired
    private TipoProductoRepository tipoProductoRepository;

    private Long savedTipoId;

    @BeforeEach
    void init() {
        TipoProducto tipo = TipoProducto.builder().tipo("Camisa").descripcion("Ropa superior").build();
        tipoProductoRepository.save(tipo);
        savedTipoId = tipo.getIdTipoProducto();
    }

    @AfterEach
    void clean() {
        tipoProductoRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<TipoProducto> tipo = tipoProductoRepository.findById(savedTipoId);
        assertTrue(tipo.isPresent());
        assertEquals("Camisa", tipo.get().getTipo());
    }

    @Test
    void testFindAll() {
        List<TipoProducto> tipos = tipoProductoRepository.findAll();
        assertNotNull(tipos);
        assertFalse(tipos.isEmpty());
    }

    @Test
    void testSave() {
        TipoProducto tipo = TipoProducto.builder().tipo("Zapato").descripcion("Calzado").build();
        TipoProducto saved = tipoProductoRepository.save(tipo);
        assertNotNull(saved.getIdTipoProducto());
        assertEquals("Zapato", saved.getTipo());
    }

    @Test
    void testUpdate() {
        Optional<TipoProducto> tipoOpt = tipoProductoRepository.findById(savedTipoId);
        assertTrue(tipoOpt.isPresent());
        TipoProducto tipo = tipoOpt.get();
        tipo.setDescripcion("Ropa formal");
        tipoProductoRepository.save(tipo);

        Optional<TipoProducto> updated = tipoProductoRepository.findById(savedTipoId);
        assertTrue(updated.isPresent());
        assertEquals("Ropa formal", updated.get().getDescripcion());
    }

    @Test
    void testDelete() {
        Optional<TipoProducto> tipoOpt = tipoProductoRepository.findById(savedTipoId);
        assertTrue(tipoOpt.isPresent());
        tipoProductoRepository.delete(tipoOpt.get());
        Optional<TipoProducto> deleted = tipoProductoRepository.findById(savedTipoId);
        assertFalse(deleted.isPresent());
    }
}
