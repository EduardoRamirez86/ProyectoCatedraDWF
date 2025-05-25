package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPuntos;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HistorialPuntosRepositoryTest {

    @Autowired
    private HistorialPuntosRepository historialPuntosRepository;

    private Long savedHistorialPuntosId;

    @BeforeEach
    void init() {
        HistorialPuntos historial = new HistorialPuntos();
        historial.setCantidadAnterior(10);
        historial.setCantidadNueva(20);
        historialPuntosRepository.save(historial);
        savedHistorialPuntosId = historial.getIdHistorialPuntos();
    }

    @AfterEach
    void clean() {
        historialPuntosRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<HistorialPuntos> historial = historialPuntosRepository.findById(savedHistorialPuntosId);
        assertTrue(historial.isPresent());
        assertEquals(10, historial.get().getCantidadAnterior());
        assertEquals(20, historial.get().getCantidadNueva());
    }

    @Test
    void testFindAll() {
        List<HistorialPuntos> historiales = historialPuntosRepository.findAll();
        assertNotNull(historiales);
        assertFalse(historiales.isEmpty());
    }

    @Test
    void testSave() {
        HistorialPuntos historial = new HistorialPuntos();
        historial.setCantidadAnterior(30);
        historial.setCantidadNueva(40);
        HistorialPuntos saved = historialPuntosRepository.save(historial);
        assertNotNull(saved.getIdHistorialPuntos());
        assertEquals(30, saved.getCantidadAnterior());
        assertEquals(40, saved.getCantidadNueva());
    }

    @Test
    void testUpdate() {
        Optional<HistorialPuntos> historialOpt = historialPuntosRepository.findById(savedHistorialPuntosId);
        assertTrue(historialOpt.isPresent());
        HistorialPuntos historial = historialOpt.get();
        historial.setCantidadNueva(50);
        historialPuntosRepository.save(historial);

        Optional<HistorialPuntos> updated = historialPuntosRepository.findById(savedHistorialPuntosId);
        assertTrue(updated.isPresent());
        assertEquals(50, updated.get().getCantidadNueva());
    }

    @Test
    void testDelete() {
        Optional<HistorialPuntos> historialOpt = historialPuntosRepository.findById(savedHistorialPuntosId);
        assertTrue(historialOpt.isPresent());
        historialPuntosRepository.delete(historialOpt.get());
        Optional<HistorialPuntos> deleted = historialPuntosRepository.findById(savedHistorialPuntosId);
        assertFalse(deleted.isPresent());
    }
}
